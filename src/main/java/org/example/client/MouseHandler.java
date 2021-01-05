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
    private final Pair offset;
    /**
     * In GUI, each field is a circle, this object helps determining whether a mouseclick happened within a field
     * or outside of it.
     */
    private final Ellipse2D.Double fieldDisk;
    private Pair start;

    /**
     * Instantiates this class with specified field dimension, diameter and no offset
     *
     * @param fieldDim dimension of a single field
     * @param diameter diameter of a clickable circle withing a single field
     */
    public MouseHandler(Dimension fieldDim, int diameter) {
        this(fieldDim, diameter, new Pair(0, 0));
    }

    /**
     * Instantiates this class with specified field dimension, diameter and offset
     *
     * @param fieldDim dimension of a single field
     * @param diameter diameter of a clickable circle withing a single field
     * @param offset   use this if fields are shifted by offset, in pixels
     */
    public MouseHandler(Dimension fieldDim, int diameter, Pair offset) {
        this.offset = offset;
        this.fieldDim = fieldDim;
        fieldDisk = new Ellipse2D.Double(((double) fieldDim.width - diameter) / 2,
                ((double) fieldDim.height - diameter) / 2, diameter, diameter);
    }

    private Pair getPosition(Point p) {
        if (fieldDisk.contains(p.x % fieldDim.width, p.y % fieldDim.height))
            return new Pair((p.x - offset.first) / fieldDim.width, (p.y - offset.second) / fieldDim.height);
        else
            return null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        start = getPosition(e.getPoint());
        if (start == null || !startCheck(start))
            return; //player did not press on any field, or pressed on a field that they do not have a pawn on
        setFieldSelection(start, true);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setFieldSelection(start, false);
        Pair end = getPosition(e.getPoint());
        if (end == null || start.equals(end))
            return; //no need to send a movement that does not do anything
        if (endCheck(end))
            send(new Packet.PacketBuilder().code(Packet.Codes.TURN_MOVE).start(start).end(end).build());
    }

    /**
     * If gui implements selection of a field when play clicks it, it should be done through this method. <br>
     * This method is abstract so that this class does not depend on implementation of board array.
     *
     * @param pos      position to set selection to
     * @param selected selection state
     */
    protected abstract void setFieldSelection(Pair pos, boolean selected);

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
