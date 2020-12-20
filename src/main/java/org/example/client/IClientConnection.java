package org.example.client;

import org.example.connection.Packet;

public interface IClientConnection {
    void send(Packet packet);

    boolean isInitialized();
}
