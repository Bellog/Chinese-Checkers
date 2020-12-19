package org.example.client;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.example.connection.ConnectionHelper;
import org.example.connection.Packet;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class ClientConnection {

    private final AtomicReference<ObjectInputStream> input = new AtomicReference<>();
    private final AtomicReference<ObjectOutputStream> output = new AtomicReference<>();
    private final Socket socket;
    private final Client client;
    private boolean isInitialized = false;

    public ClientConnection(Client client) {
        this.client = client;
        socket = new Socket();
        new Thread(() -> {
            try {
                init();
            } catch (Exception e) {
                System.out.println("Lost connection with the server closing program");
                System.exit(0);
            }
        }).start();
    }

    private void init() throws Exception {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        String version = model.getVersion();

        try {
            socket.connect(new InetSocketAddress("localhost", ConnectionHelper.DEFAULT_PORT), 10000);
        } catch (IOException e) {
            System.out.println("Couldn't connect to any server");
            return;
        }

        try {
            //first exchange is required as objectInputStream cannot be initialized on an empty stream
            output.set(new ObjectOutputStream(this.socket.getOutputStream()));
            output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.REQUEST_RESPONSE, version));
            input.set(new ObjectInputStream(this.socket.getInputStream()));
            ConnectionHelper c = (ConnectionHelper) input.get().readUnshared();
            if (c.message != ConnectionHelper.Message.STREAM_START) {
                System.out.println("Protocol error");
                throw new Exception();
            }
            c = (ConnectionHelper) input.get().readUnshared();
            if (c.message == ConnectionHelper.Message.VERSION_MISMATCH) {
                System.out.println("Version mismatch");
                System.out.println(version + "; " + c.version);
                input.get().close();
                output.get().close();
                socket.close();
                throw new Exception();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new Exception();
        }
        System.out.println("Found a game!");
        new Thread(this::handleServerReceive).start();
        new Thread(() -> {
            try {
                output.get().reset();
                output.get().writeUnshared(new Packet.PacketBuilder().code(Packet.Codes.PLAYER_INFO).build());

                output.get().reset();
                output.get().writeUnshared(new Packet.PacketBuilder().code(Packet.Codes.BOARD_UPDATE).build());
            } catch (IOException ioException) {
                System.out.println("connection error, closing program");
                System.exit(20);
            }
        }).start();
        isInitialized = true;
    }

    public void send(int i) {

        try {
            output.get().reset();
            output.get().writeUnshared(new Packet.PacketBuilder().code(Packet.Codes.PLAYER_MOVE).value(i).build());
        } catch (IOException e) {
            System.out.println("connection error, closing program");
            System.exit(40);
        }
    }

    private void handleServerReceive() {
        while (socket.isConnected()) {
            try {
                Packet packet = (Packet) input.get().readUnshared();
                switch (packet.getCode()) {
                    case INFO, WRONG_ACTION, ACTION_FAILURE -> System.out.println(packet.getMessage());
                    case PLAYER_INFO -> client.setPlayerInfo(packet.getMessage());
                    case OPPONENT_TURN -> System.out.println("It's not your turn");
                    case PLAYER_TURN -> System.out.println("It's your turn now!");
                    case ACTION_SUCCESS, BOARD_UPDATE -> {
                        System.out.println(packet.getBoard().size());
                        client.update(packet.getBoard());
                    }
                    case GAME_END -> {
                        System.out.println(packet.getMessage());
                        socket.close();
                    }
                    case OPPONENT_MOVE -> {
                        System.out.println(packet.getMessage());
                        client.update(packet.getBoard());
                    }
                }
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                System.out.println("connection error, closing program");
                System.exit(30);
            }
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
