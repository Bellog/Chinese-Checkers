package org.example.client;

import org.example.connection.Packet;

import java.awt.*;

/**
 * Client connection interface used by Client class, removes dependency on connection type.
 */
public interface IClientConnection {
    /**
     * Initialized connection with the server
     *
     * @param fieldDim server requires this field after the connection is established
     * @throws Exception if initialization fails
     */
    void init(Dimension fieldDim) throws Exception;

    /**
     * Sends packet to the server
     *
     * @param packet packet to send
     */
    void send(Packet packet);
}
