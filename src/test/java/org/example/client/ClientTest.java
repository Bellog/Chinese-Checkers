package org.example.client;

import org.example.connection.Packet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientTest {
    private final PrintStream sysOut = System.out;

    private IClient client;

    @BeforeEach
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

    @AfterAll
    public void after() {
        System.setOut(sysOut);
    }
}
