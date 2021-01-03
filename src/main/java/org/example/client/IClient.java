package org.example.client;

import org.example.connection.Packet;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Interface of a class that helps players access the game via server.
 */
public interface IClient {

    void startGame(List<Color> colors, int playerId, List<List<Integer>> board, ImageIcon boardBackground);

    void update(List<List<Integer>> board);

    /**
     * Used by interface components (i.e GamePanel) <br>
     * Removes IClientConnection dependency from those components/
     * <br> Internally calls send from IClientConnection
     *
     * @param packet packet to send
     */
    void send(Packet packet);
}
