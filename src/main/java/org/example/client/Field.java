package org.example.client;

import org.example.Pair;

import javax.swing.*;
import java.awt.*;

/**
 * Graphic representation of a field on the board.
 */
public class Field extends JPanel {
    private final JLabel label;
    /**
     * Adherence to a player.
     */
    private final int base;
    /**
     * Coordinates of this on the board.
     */
    private final Pair position;
    /**
     * Size of this.
     */
    private final int radius = 40;
    /**
     * User that will receive the graphic representation of this.
     */
    private final Client client;
    /**
     * Occupation of this.
     */
    private int state;

    /**
     * Class constructor.
     * @param base affiliation to a player.
     * @param state occupation of this field.
     * @param position coordinates.
     * @param client user for whom this field should be painted.
     */
    public Field(int base, int state, Pair position, Client client) {
        this.client = client;
        this.base = base;
        this.state = state;
        this.position = position;
        setPreferredSize(new Dimension(client.getWidth() / 4, client.getHeight() / 4));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));
        label.setText(" ");

        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
    }

    /**
     * Paint component.
     * @param g graphics.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(client.getColors().get(base + 1));
        if (g.getColor() == Color.BLACK)
            g.setColor(Color.WHITE);
        g.fillRect(10, 10, getWidth() - 10, getHeight() - 10);

        g.setColor(client.getColors().get(state + 1));
        g.fillOval(getWidth() / 2 - radius / 2, getHeight() / 2 - radius / 2, radius, radius);
        g.setColor(Color.BLACK);
        g.drawOval(getWidth() / 2 - radius / 2, getHeight() / 2 - radius / 2, radius, radius);

    }

    /**
     * State setter.
     * @param state occupation of the field.
     */
    public void setState(int state) {
        this.state = state;
        repaint();
    }

    /**
     * Position getter.
     * @return pair of coordinates.
     */
    public Pair getPosition() {
        return position;
    }
}
