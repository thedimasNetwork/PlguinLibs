package stellar.logger;

import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.serialization.Jval;
import okhttp3.*;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/* TODO
 * Multiple argument and format as in the Arc logger
 */
@SuppressWarnings("unused")
public class DiscordLogger {
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static String webhookUrl;
    private static String serverName;

    private static final ObjectMap<LogLevel, Log.LogLevel> levels = ObjectMap.of(
            LogLevel.debug, Log.LogLevel.debug,
            LogLevel.info, Log.LogLevel.info,
            LogLevel.warn, Log.LogLevel.warn,
            LogLevel.err, Log.LogLevel.err
    );

    private static void load(String webhookUrl, String serverName) {
        DiscordLogger.webhookUrl = webhookUrl;
        DiscordLogger.serverName = serverName;
    }

    public static void log(LogLevel level, String text) {
        log(level, text, null);
    }

    public static void log(LogLevel level, String text, @Nullable Throwable th) {
        Jval json = Jval.newObject();
        json.put("content", Jval.NULL);

        int rgb = level.color.getRed();
        rgb = (rgb << 8) + level.color.getGreen();
        rgb = (rgb << 8) + level.color.getBlue();

        Jval embed = Jval.newObject();
        embed.put("title", level.name)
                .put("color", rgb)
                .put("timestamp", Instant.now().toString())
                .put("footer", Jval.newObject()
                        .put("text", serverName)
                );

        if (text != null && th != null) {
            embed.put("description", String.format("### Message\n%s\n### Traceback\n```java\n%s\n```", text, Strings.getStackTrace(th).strip()));
        } else if (text != null) {
            embed.put("description", text);
        } else if (th != null) {
            embed.put("description", "```java\n" + Strings.getStackTrace(th) + "\n```");
        }

        json.put("embeds", Jval.newArray().add(embed));

        CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .post(RequestBody.create(json.toString(), MediaType.parse("application/json"))).build();

            try (Response response = httpClient.newCall(request).execute()) {
                return response.body().string();
            } catch (Throwable t) {
                Log.err("Failed to send webhook", t);
                return null;
            }
        }).handleAsync((response, throwable) -> {
            Log.debug(json.toString());
            if (throwable != null) {
                Log.err(throwable);
                return null;
            }
            Log.debug(response);
            return null;
        });
    }

    public static void info(String text) {
        log(LogLevel.info, text);
    }

    public static void infoTag(String tag, String text) {
        log(LogLevel.info, "**[" + tag + "]** " + text);
    }

    public static void warn(String text) {
        log(LogLevel.warn, text);
    }

    public static void warnTag(String tag, String text) {
        log(LogLevel.warn, "**[" + tag + "]** " + text);
    }

    public static void err(String text) {
        log(LogLevel.err, text);
    }

    public static void err(Throwable th) {
        log(LogLevel.err, null, th);
    }

    public static void err(String text, Throwable th) {
        log(LogLevel.err, text, th);
    }

    public static void errTag(String tag, String text) {
        log(LogLevel.err, "**[" + tag + "]** " + text);
    }

    public static void debug(String text) {
        log(LogLevel.debug, text);
    }

    public static void debugTag(String tag, String text) {
        log(LogLevel.debug, "**[" + tag + "]** " + text);
    }
}