package stellar.menus.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import stellar.menus.func.MenuRunner;

@AllArgsConstructor
@Getter
@Setter
public class Button {
    private final String text;
    private final MenuRunner runner;

    public Button() {
        text = "Button";
        runner = MenuRunner.none;
    }
}
