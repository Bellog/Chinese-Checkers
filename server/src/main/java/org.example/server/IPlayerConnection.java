package org.example.server;

import org.example.connection.Packet;

/**
 * Interface for playerConnection, allows to initialize connection and send packets to the player.
 */
public interface IPlayerConnection {
    /**
     * Initializes connection between object and a client.
     *
     * @return true if initialization is successful, false otherwise
     */
    boolean init();

    void send(Packet packet);
}
