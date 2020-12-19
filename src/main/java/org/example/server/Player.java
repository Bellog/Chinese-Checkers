package org.example.server;

import org.example.connection.ConnectionHelper;
import org.example.connection.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class Player {
    private final AtomicReference<ObjectInputStream> input = new AtomicReference<>();
    private final AtomicReference<ObjectOutputStream> output = new AtomicReference<>();
    private final Socket socket;
    private final Game game;

    public Player(Socket socket, Game game) throws IOException {
        this.game = game;
        this.socket = socket;

        try {
            //first exchange is required as objectInputStream cannot be initialized on an empty stream
            output.set(new ObjectOutputStream(this.socket.getOutputStream()));
            output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.STREAM_START, game.getGameVersion()));
            input.set(new ObjectInputStream(this.socket.getInputStream()));
            ConnectionHelper c = (ConnectionHelper) input.get().readUnshared();
            if (c.message == ConnectionHelper.Message.REQUEST_RESPONSE && c.version.equals(game.getGameVersion())) {
                output.get().reset();
                output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.VERSION_MATCH, game.getGameVersion()));
            } else {
                output.get().reset();
                output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.VERSION_MISMATCH, game.getGameVersion()));
                input.get().close();
                output.get().close();
                socket.close();
                throw new IOException();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new IOException();
        }

        new Thread(this::handleInput).start();
    }

    private void handleInput() {
        while (!socket.isClosed() && socket.isConnected()) {
            try {
                Packet packet = (Packet) input.get().readUnshared();
                switch (packet.getCode()) {
                    case BOARD_UPDATE, PLAYER_MOVE -> game.handleInput(this, packet);
                    case PLAYER_INFO -> send(new Packet.PacketBuilder().code(Packet.Codes.PLAYER_INFO)
                            .message(game.getMark(this)).build());
                    default -> send(new Packet.PacketBuilder().code(Packet.Codes.WRONG_ACTION).build());
                }
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                System.out.println("Lost connection to player " + game.getMark(this) + " (input)");
                game.removePlayer(this);
                return;
            }
        }
    }

    public synchronized void send(Packet packet) {
        try {
            output.get().reset();
            output.get().writeUnshared(packet);
        } catch (IOException e) {
            System.out.println("Lost connection to player" + game.getMark(this) + " (output)");
        }
    }
}
