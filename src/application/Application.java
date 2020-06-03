package application;

import screens.Screen;

import javax.swing.*;

public class Application extends JFrame {

    public static Application instance;

    Screen[] screens;
    Screen current;

    Application() {
        super("Tetris");

        instance = this;

        // the each tile will have a resolution of 20x20 regardless of the screen's size
        setSize(400, 400); // frame.pack will exclude, included for readability
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // init whatever screens you want
        // using full package name to be explicit
        screens = new Screen[] { new screens.Start(this), new screens.game.Game(this) };

        // display first screen automatically
        loadScreen(0);

        // show window
        pack();
        setVisible(true);
    }

    public void loadScreen(int index) {
        if (current != null)
            remove(current);

        Screen target = screens[index];

        add(target);
        current = target;
        current.onApplicationFocused();
    }

    public static void main(String[] args) {
        new Application();
    }
}
