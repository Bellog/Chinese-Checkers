package org.example.connection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Stores 2 integer values.
 * TODO
 * Pair class makes it easier to send such values.
 */
public final class Pos implements Serializable {
    @JsonIgnore
    private static final long serialVersionUID = 1003L;

    public final int x;
    public final int y;

    @JsonCreator
    public Pos(@JsonProperty("x") int x, @JsonProperty("y") int y) {
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
