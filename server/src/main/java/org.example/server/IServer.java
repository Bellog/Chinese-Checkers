package org.example.server;

import org.example.connection.Packet;

/**
 * Server interface
 */
public interface IServer {

    /**
     * Handles packet sent from the player
     *
     * @param playerId player who sent the packet
     * @param packet   packet to handle
     */
    void handlePacket(String playerId, Packet packet);

    void setGameHandler(IGameHandler handler);

    /**
     * Forwards packet to player with supplied playerId
     *
     * @param player not null
     * @param packet not null
     */
    void sendToPlayer(int player, Packet packet);

    /**
     * Stops the server
     */
    void stop();
}
