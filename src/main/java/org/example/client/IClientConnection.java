package org.example.client;

import org.example.connection.Packet;

import java.awt.*;

public interface IClientConnection {
    void send(Packet packet);

    void init(Dimension fieldDim) throws Exception;
}
