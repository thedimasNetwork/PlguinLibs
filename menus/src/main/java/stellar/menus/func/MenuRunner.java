package stellar.menus.func;

import mindustry.gen.Player;

@FunctionalInterface
public interface MenuRunner {
    MenuRunner none = (menuId, option, player) -> {};

    void accept(int menuId, int option, Player player);
}