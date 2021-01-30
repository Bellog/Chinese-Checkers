package org.example.server;

import org.example.connection.Packet;

/**
 * Server connection interface used by Server class, removes dependency on connection type.
 */
public interface IServerConnection {

    /**
     * Send packet to player with specified playerId
     *
     * @param playerId sends packet to this player
     * @param packet   packet to send
     */
    void sendToPlayer(int playerId, Packet packet);

    /**
     * Connects to a client.
     *
     * @return true if a new player was added, false otherwise
     */
    boolean addPlayer();
}
