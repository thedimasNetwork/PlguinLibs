package stellar.menus;

import arc.Events;
import arc.struct.IntMap;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;

import java.util.Random;

@SuppressWarnings("unused")
public class MenuHandler {
    // First two bytes are handler id and the last two are handler id
    private static final short id = randomShort();
    private static final IntMap<MenuRunner> runners = new IntMap<>();

    private static short lastId = Short.MIN_VALUE; // 64k instead of 32k available handlers while using with negative ids

    private static short nextId() {
        return lastId++;
    }

    private static short randomShort() {
        return (short) new Random().nextInt(Short.MAX_VALUE + 1);
    }

    public static void load() {
        Events.on(EventType.MenuOptionChooseEvent.class, MenuHandler::handle);
    }

    public static int send(Player player, String title, String message, String[][] buttons, MenuRunner runner) {
        int menuId = id << 16 | nextId();
        runners.put(menuId, runner);
        Call.menu(player.con(), menuId, title, message, buttons);
        return menuId;
    }

    private static String toHex(short s) {
        return String.format("%04x", s);
    }

    public static void handle(EventType.MenuOptionChooseEvent event) {
        int menuId = event.menuId;
        int option = event.option;
        Player player = event.player;

        if (!runners.containsKey(menuId >> 16)) return; // don't handle if the menu came to another handler

        MenuRunner runner = runners.get(menuId);
        if (runner == null) {
            Log.warn("Menu runner for menu @/@ is null", toHex((short) (menuId & 0xffff)), toHex((short) (menuId >> 16)));
            return;
        }
        runner.accept(menuId, option, player);
        runners.remove(menuId);
    }

}