package org.example.server;

import org.example.connection.ConnectionHelper;
import org.example.connection.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerConnectionTest {

    private final String version = "TEST-VERSION-01";
    private Socket sock;
    private PlayerConnection conn;

    @BeforeEach
    public void setup() throws IOException {
        sock = mock(Socket.class);

        conn = new PlayerConnection(sock, version) {
            @Override
            protected void handlePacket(Packet packet) {
                // DO NOTHING
            }
        };
    }

    @Test
    public void testInit() throws IOException {
        var out = mock(ObjectOutputStream.class);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(new ConnectionHelper(ConnectionHelper.Message.REQUEST_RESPONSE, version));
        oos.close();

        byte[] rawData = buffer.toByteArray();
        var in = new ByteArrayInputStream(rawData);

        when(sock.getOutputStream()).thenReturn(out);
        when(sock.getInputStream()).thenReturn(in);

        assertTrue(conn.init());
    }

    @Test
    public void testInitFail() throws IOException {
        var out = mock(ObjectOutputStream.class);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        //different, i.e. wrong version
        oos.writeObject(new ConnectionHelper(ConnectionHelper.Message.REQUEST_RESPONSE, version + "x"));
        oos.close();

        byte[] rawData = buffer.toByteArray();
        var in = new ByteArrayInputStream(rawData);

        when(sock.getOutputStream()).thenReturn(out);
        when(sock.getInputStream()).thenReturn(in);

        assertFalse(conn.init());
    }

    @Test
    public void testSend() throws IOException, ClassNotFoundException {
        //First part of this test is a slightly modified version of testInit
        var out = new ByteArrayOutputStream();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buffer);
        oos.writeObject(new ConnectionHelper(ConnectionHelper.Message.REQUEST_RESPONSE, version));
        oos.close();

        byte[] rawData = buffer.toByteArray();
        var in = new ByteArrayInputStream(rawData);

        when(sock.getOutputStream()).thenReturn(out);
        when(sock.getInputStream()).thenReturn(in);
        when(sock.isClosed()).thenReturn(false);
        when(sock.isConnected()).thenReturn(true);

        assertTrue(conn.init());

        conn.send(new Packet.PacketBuilder().code(Packet.Codes.INFO).build());

        try {
            Thread.sleep(1000); //handleOutput may not read object from send immediately
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var ret = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));

        Object o;
        do {
            o = ret.readObject(); //handleOutput may not read object from send immediately <- reason for eofException
        } while (o.getClass() == ConnectionHelper.class);

        Packet packet = (Packet) o;
        assertEquals(Packet.Codes.INFO, packet.getCode());
    }
}
