package org.example.server;

import org.example.connection.Packet;

public interface IPlayerConnection {
    boolean init();

    void send(Packet packet);
}
