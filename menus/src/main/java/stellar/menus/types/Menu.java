package stellar.menus.types;

import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.util.Log;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import mindustry.gen.Player;
import stellar.menus.MenuHandler;
import stellar.menus.func.MenuRunner;

import static stellar.menus.MenuHandler.toHex;

@Data
@SuppressWarnings("unused")
public class Menu {
    private final Player player;
    private final String title, message;
    private final String[][] buttons;
    private final boolean followUp;
    private final int menuId;
    private final MenuRunner runner;

    @Getter(value= AccessLevel.PRIVATE)
    @Setter(value=AccessLevel.PRIVATE)
    private ObjectMap<Integer, MenuRunner> buttonRunners = new ObjectMap<>();

    public Menu show() {
        Menu menu = MenuHandler.menu(player, title, message, buttons, followUp, runner);
        menu.setButtonRunners(buttonRunners.copy());
        return menu;
    }

    public Menu followUp(String title, String message, String[][] buttons) {
        return MenuHandler.menu(player, title, message, buttons, true, runner);
    }

    public void close() {
        MenuHandler.closeMenu(player, menuId);
    }

    public Menu onButton(int option, MenuRunner runner) {
        buttonRunners.put(option, runner);
        return this; // to make it chainable
    }

    /** Handle the event with the menu runners. */
    public void handle(int menuId, int option, Player player) {
        if (buttonRunners.containsKey(option)) {
            buttonRunners.get(option).accept(menuId, option, player);
        }

        if (runner != null) {
            runner.accept(menuId, option, player);
        } else {
            Log.warn("Menu runner for menu @/@ is null", toHex((short) (menuId & 0xffff)), toHex((short) (menuId >> 16)));
        }
    }
}
