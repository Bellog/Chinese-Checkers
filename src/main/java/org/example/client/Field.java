package org.example.client;

import javax.swing.*;
import java.awt.*;

public class Field extends JPanel {
    private final JLabel label;

    public Field() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));
        label.setText(" ");

        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
    }

    public void setMark(String mark) {
        label.setText(mark);
    }
}
