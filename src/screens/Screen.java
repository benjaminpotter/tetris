package screens;

import javax.swing.*;

public class Screen extends JPanel {

    JFrame window;

    // called when this screen receives the focus
    public void onApplicationFocused() {}

    public Screen(JFrame window) {
        this.window = window;
    }
}
