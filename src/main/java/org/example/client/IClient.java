package org.example.client;

import org.example.connection.Packet;

public interface IClient {
    /**
     * Client handles this packet
     *
     * @param packet packet to handle
     */
    void receive(Packet packet);
}
