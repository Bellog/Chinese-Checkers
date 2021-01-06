package org.example.client;

import org.example.connection.Packet;

/**
 * Interface used by ClientConnection, used to send packets to the client to handle them.
 *
 * @see Packet
 */
public interface IClient {

    /**
     * Client handles this packet
     *
     * @param packet packet to handle
     */
    void handlePacket(Packet packet);
}
