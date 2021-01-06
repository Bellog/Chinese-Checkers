package org.example.client;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.example.connection.ConnectionHelper;
import org.example.connection.Packet;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Responsible for communication with the server.
 */
public class ClientConnection implements IClientConnection {

    private final AtomicReference<ObjectInputStream> input = new AtomicReference<>();
    private final AtomicReference<ObjectOutputStream> output = new AtomicReference<>();
    private final Socket socket;
    private final IClient client;
    private final ReentrantLock LOCK = new ReentrantLock();

    /**
     * Class constructor.
     *
     * @param client connected user.
     */
    public ClientConnection(IClient client) {
        this.client = client;
        socket = new Socket();
    }

    @Override
    public void init(Dimension fieldDim) throws Exception {

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        String version = model.getVersion();

        try {
            socket.connect(new InetSocketAddress("localhost", ConnectionHelper.DEFAULT_PORT), 10000);
        } catch (IOException e) {
            throw new Exception("Couldn't connect to any server");
        }

        try {
            //first exchange is required as objectInputStream cannot be initialized on an empty stream
            output.set(new ObjectOutputStream(this.socket.getOutputStream()));
            output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.REQUEST_RESPONSE, version));
            input.set(new ObjectInputStream(this.socket.getInputStream()));
            ConnectionHelper c = (ConnectionHelper) input.get().readUnshared();
            if (c.message != ConnectionHelper.Message.STREAM_START) {
                throw new Exception("Protocol error");
            }
            c = (ConnectionHelper) input.get().readUnshared();
            if (c.message == ConnectionHelper.Message.VERSION_MISMATCH) {
                input.get().close();
                output.get().close();
                socket.close();
                throw new Exception("Version mismatch: your version: " + version + ", server version: " + c.version);
            } else {
                output.get().reset();
                output.get().writeUnshared(new Packet.PacketBuilder()
                        .code(Packet.Codes.APP_INFO).fieldDim(fieldDim).build());
                output.get().flush();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new Exception();
        }

        client.receive(new Packet.PacketBuilder().code(Packet.Codes.INFO)
                .message("Found a game!").build());
        new Thread(this::handleServerInput).start();
    }

    @Override
    public synchronized void send(Packet packet) {
        new Thread(() -> {
            try {
                LOCK.lock();
                output.get().reset();
                output.get().writeUnshared(packet);
                output.get().flush();
                LOCK.unlock();
            } catch (IOException e) {
                LOCK.unlock();
                client.receive(new Packet.PacketBuilder().code(Packet.Codes.CONNECTION_LOST)
                        .message("Connection lost on send").build());
            }
        }).start();
    }

    private void handleServerInput() {
        while (socket.isConnected()) {
            try {
                Packet packet = (Packet) input.get().readObject();
                if (packet != null)
                    client.receive(packet);
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                client.receive(new Packet.PacketBuilder().code(Packet.Codes.CONNECTION_LOST)
                        .message("Connection lost on receive").build());
                return;
            }
        }
    }
}
