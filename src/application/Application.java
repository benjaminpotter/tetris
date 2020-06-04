package application;

import screens.Screen;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
        new Thread(() -> {
            setVisible(false);

            if (current != null)
                remove(current);

            Screen target = screens[index];

            add(target);
            validate();

            setVisible(true);

            current = target;
            current.onApplicationFocused();
        }).start();
    }

    public static void main(String[] args) {
        new Application();
    }
}
