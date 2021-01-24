package org.example.server;

import org.example.connection.Packet;
import org.example.server.gameModes.AbstractGameMode;
import org.example.server.replay.GameSave;

import java.awt.*;
import java.util.List;

public interface IGameHandler {
    void handleInput(int player, Packet packet);

    int getNumberOfPlayers();

    /**
     * Starts the game and notifies all players
     *
     * @param fieldDims see {@link AbstractGameMode#getBoardBackground(Dimension)} for more information
     */
    void gameStart(List<Dimension> fieldDims);

    /**
     * Adds player to the game and sends {@link Packet.Codes#GAME_START} to that player
     *
     * @param player   which player to add
     * @param fieldDim used to generate background image
     */
    void joinPlayer(int player, Dimension fieldDim);

    GameSave getSave();
}
