package org.example.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * JFrame with white background
 */
public class AppFrame extends JFrame {

    AppFrame() {
        super("Sternhalma");

        new Timer(1000 / 30, v -> repaint()).start();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showOptionDialog(AppFrame.this,
                        "EXIT", "Are you sure you want to quit?\nYou may not be able to rejoin later!",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                if (option == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });

        var contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.setBackground(Color.WHITE);
        setContentPane(contentPane);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        pack();
    }

}
