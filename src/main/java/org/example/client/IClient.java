package org.example.client;

import org.example.Pair;

import java.awt.*;
import java.util.List;

/**
 * Interface of a class that helps players access the game via server.
 */
public interface IClient {
    List<Color> getColors();

    void setColors(List<Color> colors);

    void setPlayerInfo(int value);

    void update(List<List<Pair>> board);
}
