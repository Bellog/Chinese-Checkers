package org.example;

import java.io.Serializable;

/**
 * Stores 2 integer values.
 * This program requires sending pair of numbers (position in 2d arrays or {@link org.example.client.Field}'s state
 * Pair class makes it easier to send such values.
 */
public final class Pair implements Serializable {
    private static final long serialVersionUID = 1003L;

    /**
     * First integer value.
     */
    public final int first;
    /**
     * Second integer value.
     */
    public final int second;

    /**
     * Class constructor.
     * @param first integer value.
     * @param second integer value.
     */
    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Checks if we are dealing with the same pair.
     * @param o object to compare (doesn't have to be a Pair).
     * @return true if they are the same, false if not or if they are instances of different classes.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (first != pair.first) return false;
        return second == pair.second;
    }
}
