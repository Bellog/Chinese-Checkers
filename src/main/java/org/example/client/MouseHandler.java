package org.example.client;

import org.example.Pos;
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
    private final Pos offset;
    /**
     * In GUI, each field is a circle, this object helps determining whether a mouseclick happened within a field
     * or outside of it.
     */
    private final Ellipse2D.Double fieldDisk;
    private Pos start;

    /**
     * Instantiates this class with specified field dimension, diameter and no offset
     *
     * @param fieldDim dimension of a single field
     * @param diameter diameter of a clickable circle withing a single field
     */
    public MouseHandler(Dimension fieldDim, int diameter) {
        this(fieldDim, diameter, new Pos(0, 0));
    }

    /**
     * Instantiates this class with specified field dimension, diameter and offset
     *
     * @param fieldDim dimension of a single field
     * @param diameter diameter of a clickable circle withing a single field
     * @param offset   use this if fields are shifted by offset, in pixels
     */
    public MouseHandler(Dimension fieldDim, int diameter, Pos offset) {
        this.offset = offset;
        this.fieldDim = fieldDim;
        fieldDisk = new Ellipse2D.Double(((double) fieldDim.width - diameter) / 2,
                ((double) fieldDim.height - diameter) / 2, diameter, diameter);
    }

    private Pos getPosition(Point p) {
        if (fieldDisk.contains(p.x % fieldDim.width, p.y % fieldDim.height))
            return new Pos((p.x - offset.x) / fieldDim.width, (p.y - offset.y) / fieldDim.height);
        else
            return null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        start = getPosition(e.getPoint());
        if (start == null || !startCheck(start))
            return;
        setFieldSelection(start, true);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (start == null)
            return;
        setFieldSelection(start, false);
        Pos end = getPosition(e.getPoint());
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
    protected abstract void setFieldSelection(Pos pos, boolean selected);

    /**
     * Checks whether a field is occupied by the player. <br>
     * This method is abstract so that this class does not depend on implementation of board array.
     *
     * @param pos check field at this position
     * @return true if field at pos is a field occupied by the player, false otherwise
     */
    protected abstract boolean startCheck(Pos pos);

    /**
     * Checks whether a player can move their pawn to. <br>
     * This method is abstract so that this class does not depend on implementation of board array.
     *
     * @param pos check field at this position
     * @return true if field at pos is a field player can move to (i.e. field exists), false otherwise. <br>
     * This method must not include any logic that would interfere with any gameMode
     */
    protected abstract boolean endCheck(Pos pos);

    /**
     * Sends packet to the server. <br>
     * This method is abstract so that this class does not depend on implementation of the
     * Internet protocol
     *
     * @param packet packet to send
     */
    public abstract void send(Packet packet);
}
