package org.example.client;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    AppFrame() {
        super("Sternhalma");

        new Timer(1000 / 30, v -> repaint()).start();

        setAlwaysOnTop(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        pack();
    }

//    public static void main(String[] args) {
//        new AppFrame();
//    }

}
