package stellar.menus;

import arc.Events;
import arc.struct.IntMap;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;
import stellar.menus.func.MenuRunner;
import stellar.menus.func.TextInputRunner;
import stellar.menus.ui.Button;
import stellar.menus.ui.CloseButton;

import java.util.Arrays;
import java.util.Random;

/* TODO:
 * Proper UI editor with different button types, pages and more
 * (?) Bundle support; won't happen as bundles are included in the plugin not in the libs
 */
@SuppressWarnings("unused")
public class MenuHandler {
    private static final short id = randomShort(); // first two bytes are handler id and the last two are the handler id
    private static final IntMap<MenuRunner> menuRunners = new IntMap<>();
    private static final IntMap<MenuRunner> buttonRunners = new IntMap<>();
    private static final IntMap<TextInputRunner> textInputRunners = new IntMap<>();

    private static short lastMenuId = Short.MIN_VALUE;
    private static short lastTextInputId = Short.MIN_VALUE;

    private static short nextMenuId() {
        return lastMenuId++;
    }

    private static short nextTextInputId() {
        return lastTextInputId++;
    }

    private static short randomShort() {
        return (short) new Random().nextInt(Short.MAX_VALUE + 1);
    }

    public static void load() {
        Events.on(EventType.MenuOptionChooseEvent.class, MenuHandler::handleMenu);
        Events.on(EventType.TextInputEvent.class, MenuHandler::handleTextInput);
    }

    public static int menu(Player player, String title, String message, String[][] buttons, MenuRunner runner) {
        return menu(player, title, message, buttons, false, runner);
    }

    public static int menu(Player player, String title, String message, String[][] buttons, boolean followUp, MenuRunner runner) {
        int menuId = id << 16 | nextMenuId();
        menuRunners.put(menuId, runner);
        if (followUp) {
            Call.followUpMenu(player.con(), menuId, title, message, buttons);
        } else {
            Call.menu(player.con(), menuId, title, message, buttons);
        }
        return menuId;
    }

    public static int menu(Player player, String title, String message, Button[][] buttons, MenuRunner runner) {
        String[][] strings = Arrays.stream(buttons)
                .map(b -> Arrays.stream(b).map(Button::getText).toArray(String[]::new))
                .toArray(String[][]::new);
        int menuId = menu(player, title, message, strings, runner);
        int index = 0;
        for (Button[] row : buttons) {
            for (Button b : row) {
                buttonRunners.put(menuId << 16 | index, b.getRunner());
                index++;
            }
        }
        return menuId;
    }

    public static void closeMenu(Player player, int menuId) {
        Call.hideFollowUpMenu(player.con(), menuId);
    }

    public static int textInput(Player player, String title, String message, int length, String def, boolean numeric, TextInputRunner runner) {
        int textInputId = id << 16 | nextTextInputId();
        textInputRunners.put(textInputId, runner);
        Call.textInput(player.con(), textInputId, title, message, length, def, numeric);
        return textInputId;
    }

    private static String toHex(short s) {
        return String.format("%04x", s);
    }

    private static void handleMenu(EventType.MenuOptionChooseEvent event) {
        int menuId = event.menuId;
        int option = event.option;
        Player player = event.player;

        if (!menuRunners.containsKey(menuId >> 16)) return; // don't handle if the menu came to another handler

        MenuRunner runner = menuRunners.get(menuId);
        if (runner == null) {
            Log.warn("Menu runner for menu @/@ is null", toHex((short) (menuId & 0xffff)), toHex((short) (menuId >> 16)));
            return;
        }
        runner.accept(menuId, option, player);
        menuRunners.remove(menuId);
    }

    private static void handleTextInput(EventType.TextInputEvent event) {
        int textInputId = event.textInputId;
        String text = event.text;
        Player player = event.player;

        if (!textInputRunners.containsKey(textInputId >> 16)) return; // don't handle if the text input came to another handler

        TextInputRunner runner = textInputRunners.get(textInputId);
        if (runner == null) {
            Log.warn("Text input runner for text input @/@ is null", toHex((short) (textInputId & 0xffff)), toHex((short) (textInputId >> 16)));
            return;
        }
        runner.accept(textInputId, text, player);
        textInputRunners.remove(textInputId);
    }
}