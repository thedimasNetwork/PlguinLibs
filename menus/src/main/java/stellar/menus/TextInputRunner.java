package stellar.menus;

import mindustry.gen.Player;

@FunctionalInterface
public interface TextInputRunner {
    void accept(int textInputId, String text, Player player);
}
