package org.example.server;

import org.example.connection.Packet;

/**
 * Server interface
 */
public interface IServer {

    /**
     * Handles packet sent from the player, usually used by IServerConnection
     *
     * @param playerId player who sent the packet
     * @param packet   packet to handle
     */
    void handlePacket(int playerId, Packet packet);

    /**
     * Forwards packet to player with supplied playerId
     *
     * @param playerId playerId is not checked whether it is valid.
     * @param packet   not null
     */
    void sendToPlayer(int playerId, Packet packet);

    /**
     * Stops the server
     */
    void stop();

    /**
     * retu
     *
     * @return non null String
     */
    String getVersion();
}
