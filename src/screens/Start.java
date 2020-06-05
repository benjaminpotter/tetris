package screens;

import application.Application;
import io.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Start extends Screen {

    JLabel highscore;

    public Start(JFrame window) {
        super(window);

        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.white);

        setLayout(new BorderLayout());

        BufferedImage logo = Resources.LoadImage("assets/tetris.png");
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(logo, 0, 0, null);
            }
        };
        add(logoPanel, BorderLayout.CENTER);

        JPanel menu = new JPanel();

        highscore = new JLabel("");
        menu.add(highscore);

        JButton start = new JButton("Play!");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // load game
                Application.instance.loadScreen(1);
            }
        });
        menu.add(start);
        add(menu, BorderLayout.SOUTH);
    }

    @Override
    public void onApplicationFocused() {
        super.onApplicationFocused();

        highscore.setText("Highscore: " + String.valueOf(Resources.LoadInt("highscore.txt")));
    }
}
