package org.example.client;

import org.example.Pair;

import java.util.List;
import java.awt.*;

/**
 * Graphic representation of a field on the board.
 */
public class Field {
    private final Pair pos;
    private boolean selected = false; //draw indication that client clicked on this field
    private List<List<Pair>> board;

    public Field(Pair pos, List<List<Pair>> board) {
        this.pos = pos;
        this.board = board;
    }

    /**
     * Class constructor.
     *
     * @param type     affiliation to a player.
     * @param state    occupation of this field.
     * @param position coordinates.
     * @param client   user for whom this field should be painted.
     */
    public Field(int type, int state, Pair position, Client client) {
//        this.client = client;
        pos = new Pair(0, 0);
//        this.type = type;
//        this.state = state;
//        this.position = position;
    }

    public int getType() {
        return board.get(pos.second).get(pos.first).second;
    }

    public int getState() {
        return board.get(pos.second).get(pos.first).first;
    }

    public void setState(int state) {
        board.get(pos.second).set(pos.first, new Pair(state, board.get(pos.second).get(pos.first).second));
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
