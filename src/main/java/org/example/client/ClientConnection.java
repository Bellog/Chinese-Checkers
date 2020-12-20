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

/**
 * Responsible for communication with the server.
 */
public class ClientConnection implements IClientConnection {

    /**
     * Input.
     */
    private final AtomicReference<ObjectInputStream> input = new AtomicReference<>();
    /**
     * Output.
     */
    private final AtomicReference<ObjectOutputStream> output = new AtomicReference<>();
    /**
     * Connection.
     */
    private final Socket socket;
    /**
     * Operated user.
     */
    private final Client client;
    /**
     * Makes sure the new Client in a thread is initialised.
     */
    private volatile boolean isInitialized = false;

    /**
     * Class constructor.
     * @param client connected user.
     */
    public ClientConnection(Client client) {
        this.client = client;
        socket = new Socket();
        new Thread(() -> {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Lost connection with the server closing program");
                System.exit(0);
            }
        }).start();
    }

    /**
     * Initialises a connection between user and the server.
     * @throws Exception
     */
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
                output.get().writeUnshared(new Packet.PacketBuilder().code(Packet.Codes.BOARD_UPDATE).build());

                output.get().reset();
                output.get().writeUnshared(new Packet.PacketBuilder().code(Packet.Codes.PLAYER_INFO).build());

                output.get().reset();
                output.get().writeUnshared(new Packet.PacketBuilder().code(Packet.Codes.PLAYER_COLORS).build());
                isInitialized = true;
            } catch (IOException ioException) {
                System.out.println("connection error, closing program");
                System.exit(20);
            }
        }).start();
    }

    /**
     * Transfers data about the game.
     * @param packet data.
     */
    @Override
    public void send(Packet packet) {
        try {
            output.get().reset();
            output.get().writeUnshared(packet);
        } catch (IOException e) {
            System.out.println("connection error, closing program");
            System.exit(40);
        }
    }

    /**
     * Interpretation of messages from the server.
     */
    private void handleServerReceive() {
        while (socket.isConnected()) {
            try {
                Packet packet = (Packet) input.get().readUnshared();
                switch (packet.getCode()) {
                    case INFO, WRONG_ACTION, ACTION_FAILURE -> System.out.println(packet.getMessage());
                    case PLAYER_COLORS -> client.setColors(packet.getColors());
                    case PLAYER_INFO -> client.setPlayerInfo(packet.getValue());
                    case OPPONENT_TURN -> System.out.println("It's not your turn");
                    case PLAYER_TURN -> System.out.println("It's your turn now!");
                    case ACTION_SUCCESS, BOARD_UPDATE -> client.update(packet.getBoard());
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

    /**
     * Checking-variable getter.
     * @return true if a connection is initialised, false otherwise.
     */
    @Override
    public boolean isInitialized() {
        return isInitialized;
    }
}
