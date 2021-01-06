package org.example;

import java.io.Serializable;

/**
 * Stores 2 integer values.
 * This program requires sending pair of numbers (position in 2d arrays or {@link org.example.client.Field}'s state
 * Pair class makes it easier to send such values.
 */
public final class Pos implements Serializable {
    private static final long serialVersionUID = 1003L;

    public final int x;
    public final int y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pos pos = (Pos) o;

        if (x != pos.x) return false;
        return y == pos.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
