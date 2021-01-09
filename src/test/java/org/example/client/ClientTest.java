package org.example.client;

import org.example.connection.Packet;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class ClientTest {

    private IClient client;

    @Before
    public void setup() {
        IClientConnection conn = mock(ClientConnection.class);

        client = new Client(conn);

    }

    @Test
    public void logTest() {
        // check if client properly handles logging if there is not gui
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertEquals(0, out.size());
        System.setOut(new PrintStream(out, false));
        client.handlePacket(new Packet.PacketBuilder().code(Packet.Codes.INFO).message("1234567890").build());
        assertEquals(12, out.size());
    }
}
