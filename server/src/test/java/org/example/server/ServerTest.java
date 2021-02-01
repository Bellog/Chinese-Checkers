package org.example.server;

import org.example.connection.Packet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerTest {

    private Server server;
    private GameHandler handler;

    @BeforeAll
    public void classSetup() {
        handler = mock(GameHandler.class);
        server = new Server();
        server.setGameHandler(handler);
    }

    @Test
    void handlePacket() {
    }

    @Test
    void sendToPlayer() {
    }

    @Test
    void sendToAllExcept() {
    }

    @Test
    void stop() {
    }

    @Test
    void testAddPlayer() {
        server.handlePacket("0", new Packet.PacketBuilder().code(Packet.Codes.GAME_PAUSE).build());
    }
}