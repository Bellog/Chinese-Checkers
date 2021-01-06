package org.example.server;

import org.example.connection.Packet;

public interface IServer {

    void handlePacket(int playerId, Packet packet);

    void sendToPlayer(int playerId, Packet packet);

    void stop();

    String getVersion();
}
