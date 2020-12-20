package org.example.server;

import org.example.Pair;

import java.awt.*;
import java.util.List;

public interface IGame {
    int getBoardHeight();

    int getBoardWidth();

    int getNumberOfPlayers();

    Pair getFieldInfo(int x, int y);

    boolean move(int x0, int y0, int x1, int y1);

    boolean hasWinner();

    List<Color> getColors();
}
