package org.example.server;

import org.example.connection.Packet;
import org.example.server.replay.GameSave;

import java.awt.*;

public interface IGameHandler {
    void handleInput(int player, Packet packet);

    int getNumberOfPlayers();

    /**
     * Starts the game and notifies all players
     */
    void gameStart();

    /**
     * Adds player to the game and sends {@link Packet.Codes#GAME_SETUP} to that player
     *
     * @param player   which player to add
     * @param fieldDim used to generate background image
     */
    void joinPlayer(int player, Dimension fieldDim);

    GameSave getSave();
}
