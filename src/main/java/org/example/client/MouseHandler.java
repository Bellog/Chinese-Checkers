package org.example.client;

import org.example.Pair;
import org.example.connection.Packet;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
 * Mouse adapter class for game panel. <br>
 * This class assumes that there is no gap between individual fields and that game field starts at (x,y) = (0,0). <br>
 * In addition to that this class assumes that actual field is a circle at the center of a field specified by fieldDim
 */
public abstract class MouseHandler extends MouseAdapter {
    /**
     * Dimensions of a field in client GUI, used to determine position of a field that player clicked
     */
    private final Dimension fieldDim;
    /**
     * In GUI, each field is a circle, this object helps determining whether a mouseclick happened within a field
     * or outside of it.
     */
    private final Ellipse2D.Double fieldDisk;
    private Pair start;

    /**
     * Instantiates this class with specified field dimension and field diameter
     *
     * @param fieldDim dimension of a single field
     * @param diameter diameter of a clickable circle withing a single field
     */
    public MouseHandler(Dimension fieldDim, int diameter) {
        this.fieldDim = fieldDim;
        fieldDisk = new Ellipse2D.Double(((double) fieldDim.width - diameter) / 2,
                ((double) fieldDim.height - diameter) / 2, diameter, diameter);
    }

    private Pair getPosition(Point p) {
        System.out.println(fieldDisk.contains(p.x % fieldDim.width, p.y % fieldDim.height) + " - " +
                           p.x % fieldDim.width + " and " + p.y % fieldDim.height);
        if (fieldDisk.contains(p.x % fieldDim.width, p.y % fieldDim.height))
            return new Pair(p.x / fieldDim.width, p.y / fieldDim.height);
        else
            return null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.print("\nstart: ");
        start = getPosition(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("check 1");
        if (start == null || !startCheck(start))
            return; //player did not press on any field, or pressed on a field that they do not have a pawn on
        Pair end = getPosition(e.getPoint());
        System.out.println("check 2");
        if (end == null || start.equals(end))
            return; //no need to send a movement that does not do anything
        System.out.println("check 3");
        if (endCheck(end))
            send(new Packet.PacketBuilder().code(Packet.Codes.PLAYER_MOVE).start(start).end(end).build());
    }

    /**
     * Checks whether a field is occupied by the player. <br>
     * This method is abstract so that this class does not depend on implementation of board array.
     *
     * @param pos check field at this position
     * @return true if field at pos is a field occupied by the player, false otherwise
     */
    protected abstract boolean startCheck(Pair pos);

    /**
     * Checks whether a player can move their pawn to. <br>
     * This method is abstract so that this class does not depend on implementation of board array.
     *
     * @param pos check field at this position
     * @return true if field at pos is a field player can move to (i.e. field exists), false otherwise. <br>
     * This method must not include any logic that would interfere with any gameMode
     */
    protected abstract boolean endCheck(Pair pos);

    /**
     * Sends packet to the server. <br>
     * This method is abstract so that this class does not depend on implementation of the
     * Internet protocol
     *
     * @param packet
     */
    public abstract void send(Packet packet);
}
