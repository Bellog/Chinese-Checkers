package org.example.server;

import org.example.connection.Packet;

public interface IServerConnection {

    /**
     * Send packet to player with specified playerId
     *
     * @param playerId sends packet to this player
     * @param packet   packet to send
     * @throws Exception if packet cannot be sent, reason is specified in the exception's message
     */
    void sendToPlayer(int playerId, Packet packet);

    boolean addPlayer();
}
