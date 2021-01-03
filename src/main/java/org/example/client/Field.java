package org.example.client;

/**
 * Graphic representation of a field on the board.
 */
public class Field {
    private int state;
    private boolean selected = false; //draw indication that client clicked on this field

    public Field(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
