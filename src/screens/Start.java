package screens;

import application.Application;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Start extends Screen {


    public Start(JFrame window) {
        super(window);

        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.white);

        JButton start = new JButton("Play!");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // load game
                Application.instance.loadScreen(1);
            }
        });
        add(start);
    }
}
