package org.example.server;

import org.example.Pair;

import java.awt.*;
import java.util.List;

/**
 * Various types of games implement from this.
 */
public interface IGame {
    /**
     * Height getter.
     * @return vertical dimension of the board.
     */
    int getBoardHeight();

    /**
     * Width getter.
     * @return horizontal dimension of the board.
     */
    int getBoardWidth();

    /**
     * Number getter.
     * @return required number of players.
     */
    int getNumberOfPlayers();

    /**
     * Used to access information about occupation and significance of fields on board.
     * @param x coordinate
     * @param y coordinate
     * @return state and base of a field, null if no such coordinates were found.
     */
    Pair getFieldInfo(int x, int y);

    /**
     * Rules should be defined in a separate class and be used here to determine outcome of the move
     *
     * @return true if move is successful, false if not
     */
    boolean move(int x0, int y0, int x1, int y1);

    /**
     * Win condition.
     * @return true if a situation on the board is game-ending, false otherwise.
     */
    boolean hasWinner();

    /**
     * Colors getter.
     * @return list of colors.
     */
    List<Color> getColors();
}
