package org.example.server;

import org.example.Pair;

import java.awt.*;
import java.util.List;

/**
 * Interface used to implement various game modes.
 */
public interface IGame {

    int getBoardHeight();

    int getBoardWidth();

    int getNumberOfPlayers();

    /**
     * Used to access information about state and type of fields on board.
     *
     * @param x coordinate
     * @param y coordinate
     * @return state and type of a field, null if no such coordinates were found.
     */
    Pair getFieldInfo(int x, int y);

    /**
     * Moves pawn from (x0, y0) to (x1, y1).
     * Assumes that it's pawn owner's turn
     * Checks if move is possible.
     *
     * @return true if move is successful, false if not
     */
    boolean move(int x0, int y0, int x1, int y1);

    boolean hasWinner();

    /**
     * Returns list in certain order: no player, player0, player1, etc.
     * if field's state is x then it's corresponding color is at x+1
     *
     * @return list of colors.
     */
    List<Color> getColors();
}
