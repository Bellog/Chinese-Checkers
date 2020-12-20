package org.example.client;

import org.example.Pair;

import java.awt.*;
import java.util.List;

public interface IClient {
    List<Color> getColors();

    void setColors(List<Color> colors);

    void setPlayerInfo(int value);

    void update(List<List<Pair>> board);
}
