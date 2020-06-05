package screens;

import application.Application;
import screens.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Death extends Screen {


    public Death(JFrame window) {
        super(window);

        setPreferredSize(new Dimension(400, 400));

        JLabel deathMessage = new JLabel("You DIED!");
        add(deathMessage);

        JButton start = new JButton("Back to Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.instance.loadScreen(0);
            }
        });
        add(start);
    }
}
