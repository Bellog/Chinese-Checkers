package org.example.client;

import org.example.Pair;

import java.awt.*;
import java.util.List;

/**
 * Interface of a class that helps players access the game via server.
 */
public interface IClient {
    /**
     * Color-list getter.
     * @return list of colors.
     */
    List<Color> getColors();

    /**
     * Color-list setter.
     * @param colors list of colors.
     */
    void setColors(List<Color> colors);

    /**
     * For a player to get information about themselfs in a game.
     * @param value number of a player.
     */
    void setPlayerInfo(int value);

    /**
     * Generates graphics for players.
     * @param board representation of the board.
     */
    void update(List<List<Pair>> board);
}
