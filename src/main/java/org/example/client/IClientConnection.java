package org.example.client;

import org.example.connection.Packet;

public interface IClientConnection {
    /**
     * Transfers data about the game.
     * @param packet data.
     */
    void send(Packet packet);

    /**
     * Checking-variable getter.
     * @return true if a connection is initialised, false otherwise.
     */
    boolean isInitialized();
}
