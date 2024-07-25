package stellar.menus.ui;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import stellar.menus.MenuHandler;
import stellar.menus.func.MenuRunner;


public class CloseButton extends Button { // TODO: bundles
    public CloseButton() {
        super("[red]Close[]", (menuId, option, player) -> MenuHandler.closeMenu(player, menuId));
    }

    private CloseButton(String text, MenuRunner runner) {}
}
