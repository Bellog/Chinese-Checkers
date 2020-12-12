package org.example.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class Player {
    private final String mark;
    private final AtomicReference<ObjectInputStream> input = new AtomicReference<>();
    private final AtomicReference<ObjectOutputStream> output = new AtomicReference<>();
    private final Socket socket;
    private final Game game;


    public Player(String mark, Socket socket, Game game) throws Exception {
        this.game = game;
        this.mark = mark;
        this.socket = socket;
        try {
            //first exchange is required as objectInputStream cannot be initialized on an empty stream
            output.set(new ObjectOutputStream(this.socket.getOutputStream()));
            output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.STREAM_START, game.getGameVersion()));
            input.set(new ObjectInputStream(this.socket.getInputStream()));
            ConnectionHelper c = (ConnectionHelper) input.get().readUnshared();
            if (c.message == ConnectionHelper.Message.REQUEST_RESPONE && c.version.equals(game.getGameVersion())) {
                output.get().reset();
                output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.VERSION_MATCH, game.getGameVersion()));
            } else {
                output.get().reset();
                output.get().writeUnshared(new ConnectionHelper(ConnectionHelper.Message.VERSION_MISMATCH, game.getGameVersion()));
                input.get().close();
                output.get().close();
                socket.close();
                throw new Exception();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new Exception();
        }

        new Thread(this::handleInput).start();
    }

    private void handleInput() {
        while (socket.isConnected()) {
            try {
                Packet packet = (Packet) input.get().readUnshared();
                switch (packet.getCode()) {
                    case BOARD_UPDATE, PLAYER_MOVE -> game.handleInput(this, packet);
                    default -> send(new Packet.PacketBuilder().code(Packet.Codes.WRONG_ACTION).build());
                }
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void send(Packet packet) {
        try {
            output.get().reset();
            output.get().writeUnshared(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMark() {
        return mark;
    }
}
