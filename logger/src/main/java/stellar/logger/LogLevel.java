package stellar.logger;

import java.awt.*;

public enum LogLevel {

    debug("Debug", new Color(0x43B581)),
    info("Info", new Color(0x7289DA)),
    warn("Warn", new Color(0xFAA61A)),
    err("Error", new Color(0xF04747));

    public final String name;
    public final Color color;

    LogLevel(String name, Color color) {
        this.name = name;
        this.color = color;
    }
}