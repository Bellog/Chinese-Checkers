package org.example.server;

import org.example.Pair;
import org.example.connection.Packet;
import org.example.server.gameModes.AbstractGameMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GameHandler {

    private final AbstractGameMode game;
    private final ReentrantLock LOCK = new ReentrantLock();
    private int currentPlayer;

    /**
     * Class constructor.
     *
     * @param game type of game that will be played.
     */
    public GameHandler(AbstractGameMode game) {
        this.game = game;

        var rand = new Random();
        currentPlayer = rand.nextInt(game.getNumberOfPlayers());
    }

    public int getNumberOfPlayers() {
        return game.getNumberOfPlayers();
    }

    public void gameStart() {
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            //TODO get dimension from player
            sendToPlayer(i, new Packet.PacketBuilder()
                    .code(Packet.Codes.GAME_START).colorScheme(game.getColorScheme())
                    .board(game.getBoard())
                    .playerId(i)
                    .image(game.getBoardBackground(new Dimension(28, 48)))
                    .playerInfo(generatePlayerInfo(i))
                    .build());
        }
        startTurn();
    }

    private List<List<String>> generatePlayerInfo(int playerId) {
        List<List<String>> list = new ArrayList<>();

        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            list.add(new ArrayList<>());

            if (i == playerId)
                list.get(i).add("Player " + (i + 1) + " (You)");
            else
                list.get(i).add("Player " + (i + 1));

            list.get(i).add("-"); //position
        }

        List<String> header = new ArrayList<>();
        header.add("Player");
        header.add("Pos");
        list.add(0, header);

        return list;
    }

    public void handleInput(int player, Packet packet) {
        LOCK.lock();
        if (player != currentPlayer) {
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.OPPONENT_TURN)
                    .message("It's not your turn!").build());
            return;
        }

        switch (packet.getCode()) {
            case TURN_MOVE -> {
                if (move(packet.getStartPos(), packet.getEndPos()))
                    for (int i = 0; i < game.getNumberOfPlayers(); i++)
                        sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.BOARD_UPDATE)
                                .board(game.getBoard()).build());
            }
            case TURN_END -> endTurn();
            case TURN_ROLLBACK -> game.rollBack();
        }
        LOCK.unlock();
    }

    private void startTurn() {
        sendToPlayer(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.TURN_START)
                .message("It's your turn now!").build());
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            if (i != currentPlayer)
                sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                        .message("Player " + (currentPlayer + 1) + " turn").build());
        }
    }

    private void endTurn() {
        sendToPlayer(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.TURN_END).build());
        currentPlayer = (currentPlayer + 1) % game.getNumberOfPlayers();
        game.endTurn();
        startTurn();
    }

    public boolean move(Pair start, Pair end) {
        if (game.getFieldInfo(start.first, start.second) != currentPlayer) {
            sendToPlayer(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.ACTION_FAILURE)
                    .message("You cannot move somebody else's pawn!").build());
            return false;
        }
        if (game.canMove(start)) {
            if (game.move(start, end)) {
                for (int i = 0; i < game.getNumberOfPlayers(); i++)
                    if (i != currentPlayer)
                        sendToPlayer(i, new Packet.PacketBuilder()
                                .code(Packet.Codes.OPPONENT_MOVE).board(game.getBoard())
                                .message("Player " + (currentPlayer + 1) + " moved").build());
                return true;
            } else {
                sendToPlayer(currentPlayer, new Packet.PacketBuilder()
                        .code(Packet.Codes.ACTION_FAILURE).message("This field is already set").build());
                return false;
            }
        } else {
            sendToPlayer(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("You cannot move from this field").build());
            return false;
        }
    }

    protected abstract void sendToPlayer(int player, Packet packet);
}
