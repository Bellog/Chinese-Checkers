package org.example.client;

import org.example.Pair;

import javax.swing.*;
import java.awt.*;

/**
 * Graphic representation of a field on the board.
 */
public class Field extends JPanel {
    private final int type;
    private final Pair position;
    private final int radius = 40;
    private final Client client;

    private int state;

    /**
     * Class constructor.
     *
     * @param type     affiliation to a player.
     * @param state    occupation of this field.
     * @param position coordinates.
     * @param client   user for whom this field should be painted.
     */
    public Field(int type, int state, Pair position, Client client) {
        this.client = client;
        this.type = type;
        this.state = state;
        this.position = position;
        setPreferredSize(new Dimension(client.getWidth() / 4, client.getHeight() / 4));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(client.getColors().get(type + 1));
        if (g.getColor() == Color.BLACK)
            g.setColor(Color.WHITE);
        g.fillRect(10, 10, getWidth() - 10, getHeight() - 10);

        g.setColor(client.getColors().get(state + 1));
        g.fillOval(getWidth() / 2 - radius / 2, getHeight() / 2 - radius / 2, radius, radius);
        g.setColor(Color.BLACK);
        g.drawOval(getWidth() / 2 - radius / 2, getHeight() / 2 - radius / 2, radius, radius);

    }

    public void setState(int state) {
        this.state = state;
        repaint();
    }

    public Pair getPosition() {
        return position;
    }
}
