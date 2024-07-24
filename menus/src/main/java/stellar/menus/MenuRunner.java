package stellar.menus;

import mindustry.gen.Player;

@FunctionalInterface
public interface MenuRunner {
    void accept(int menuId, int option, Player player);
}