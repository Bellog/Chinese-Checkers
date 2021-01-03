package org.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO make methods/classes for easier changes to fields' state

/**
 * Abstract rule set for Sternhalma game. <br>
 * Stores board as 2-dimensional list of Pair objects, where each pair is: (state, type) (see getBoard() method
 * for more information.
 * <p></p>
 * Every child has to populate board with default data during its initialization, i.e starting board
 */
public abstract class ARuleSet implements Serializable {
    protected final List<List<Pair>> board = new ArrayList<>();

    /**
     * Returns list of neighbors of a field at pos position.
     * <br>This method can used for drawing purposes or as a helper function for getPossibleMovesMethod(Pair) method,
     * It should not define rules for pawn movements.
     * <p></p>
     * This method should use neighborCheck internally.
     * <ul>
     * Logic in this method determines which field can be neighbors, not whether they are valid neighbors.
     * <br><br>
     * If a neighbor is not valid, it should be a null, so that this method returns arrays of constant size.</ul>
     *
     * @param pos position (x, y) of a field to check its neighbors
     * @return Returns list of (nullable )neighbors of a field at pos position
     */
    public abstract List<Pair> getNeighbors(Pair pos);

    /**
     * Helper method for getNeighbors(Pair) method.
     *
     * @param pos     position of a field to check
     * @param xOffset checks neighbor at pos.first + xOffset
     * @param yOffset checks neighbor at pos.second + xOffset
     * @return Returns new Pair with neighbor position or null if neighbors is null
     */
    protected final Pair neighborCheck(Pair pos, int xOffset, int yOffset) {
        // field at pos + offset is withing bounds and is not null
        if (pos.first + xOffset < board.get(0).size() && pos.first + xOffset >= 0 &&
            pos.second + yOffset < board.size() && pos.second + yOffset >= 0 &&
            board.get(pos.second + yOffset).get(pos.first + xOffset) != null)
            return new Pair(pos.first + xOffset, pos.second + yOffset);
        else
            return null;
    }

    /**
     * List of field where pawn at pos can move.
     * <p></p>
     * This method should be aware of pawn's last movements
     *
     * @param pos position of a pawn to check
     * @return List of field where player can move, null if there is no pawn at specified position
     */
    public abstract List<Pair> getPossibleMoves(Pair pos);

    /**
     * Returns game board, where each field is a Pair of (state, type)
     * <p></p>
     * type - base of which player (-1 means it is nobody's base, >= 0 means it is base of a player with this id
     * <p></p>
     * state - which player has a (-1 means it is nobody's base, >= 0 means it is base of a player with this id
     *
     * @return game board
     */
    public final List<List<Pair>> getBoard() {
        return board;
    }

    /**
     * Returns winner's id, -1 otherwise
     *
     * @return winner's id, -1 otherwise
     */
    public abstract int hasWinner();

}
