package stellar.menus.func;

import mindustry.gen.Player;

@FunctionalInterface
public interface TextInputRunner {
    TextInputRunner none = (textInputId, text, player) -> {};

    void accept(int textInputId, String text, Player player);
}
