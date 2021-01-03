package org.example;

import java.io.Serializable;

/**
 * Stores 2 integer values.
 * This program requires sending pair of numbers (position in 2d arrays or {@link org.example.client.Field}'s state
 * Pair class makes it easier to send such values.
 */
public final class Pair implements Serializable {
    private static final long serialVersionUID = 1003L;

    public final int first;
    public final int second;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (first != pair.first) return false;
        return second == pair.second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
