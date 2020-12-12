package org.example.connection;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Client {

    private final AtomicReference<ObjectInputStream> input = new AtomicReference<>();
    private final AtomicReference<ObjectOutputStream> output = new AtomicReference<>();
    private final Socket socket;

    public Client() throws Exception {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        String version = model.getVersion();

        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("localhost", ConnectionHelper.DEFAULT_PORT), 10000);
        } catch (IOException e) {
            System.out.println("Couldn't connect to any server");
            return;
        }

        try {
            //first exchange is required as objectInputStream cannot be initialized on an empty stream
            output.set(new ObjectOutputStream(this.socket.getOutputStream()));
            output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.REQUEST_RESPONE, version));
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
        System.out.println("started");
        new Thread(this::handleInput).start();
        new Thread(this::handleOutput).start();
    }

    public static void main(String[] args) {
        System.out.println("Starting client");
        try {
            new Client();
        } catch (Exception e) {
            System.out.println("Failed to start client");
        }
    }

    private void handleOutput() {
        var cli = new Scanner(System.in);
        while (!socket.isClosed() && socket.isConnected() && cli.hasNextLine()) {
            var text = cli.next();
            try {
                int i = Integer.parseInt(text);
                output.get().reset();
                output.get().writeUnshared(new Packet.PacketBuilder().code(Packet.Codes.PLAYER_MOVE).value(i).build());
            } catch (NumberFormatException e) {
                switch (text) {
                    case "exit" -> System.exit(0);
                    case "update" -> {
                        try {
                            output.get().reset();
                            output.get().writeUnshared(new Packet.PacketBuilder().code(Packet.Codes.BOARD_UPDATE).build());
                        } catch (IOException ioException) {
                            System.out.println("connection failed");
                            System.exit(1);
                        }
                    }
                    default -> System.out.println("Incorrect input");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput() {
        while (!socket.isClosed() && socket.isConnected()) {
            try {
                Packet packet = (Packet) input.get().readUnshared();
                switch (packet.getCode()) {
                    case WRONG_PLAYER -> System.out.println("Its not your turn");
                    case WRONG_ACTION -> System.out.println(packet.getMessage());
                    case ACTION_SUCCESS -> System.out.println(packet.getBoard());
                    case ACTION_FAILURE -> System.out.println(packet.getMessage());
                    case GAME_END -> {
                        System.out.println(packet.getMessage());
                        socket.close();
                    }
                    case BOARD_UPDATE -> System.out.println(packet.getBoard());
                    case OPPONENT_MOVE -> {
                        System.out.println(packet.getMessage());
                        System.out.println(packet.getBoard());
                    }
                }
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
            }
        }
    }
}
