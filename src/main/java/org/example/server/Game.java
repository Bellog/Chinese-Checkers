package org.example.server;

import org.example.connection.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

//TODO move handler (this) to server class, requires proper implementation of handler, field and game classes first
public class Game {

    private final List<Field> board = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final List<String> marks = List.of("x", "o", "#");
    private final int boardWidth = 4;
    private final int maxPlayers = 3;
    private final String gameVersion;
    private final Server server;
    private final ReentrantLock LOCK = new ReentrantLock();
    private int current;

    public Game(String gameVersion, Server server) {
        this.server = server;
        this.gameVersion = gameVersion;
        for (var i = 0; i < boardWidth * boardWidth; i++)
            board.add(new Field());
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void removePlayer(Player player) {
        LOCK.lock();
        String mark = getMark(player);
        players.set(players.indexOf(player), null);
        players.stream().filter(Objects::nonNull)
                .forEach(p -> sendToPlayer(p, new Packet.PacketBuilder()
                        .code(Packet.Codes.INFO).message("Lost connection to player " + mark).build()));
        LOCK.unlock();

        new Thread(() -> {
            if (!server.getNewPlayer()) {
                LOCK.lock();
                players.stream().filter(Objects::nonNull)
                        .forEach(p -> sendToPlayer(p, new Packet.PacketBuilder()
                                .code(Packet.Codes.GAME_END).message("Could not find any players").build()));
                LOCK.unlock();
            }
        }).start();
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public synchronized void addPlayer(Player player) {
        LOCK.lock();
        if (players.size() == 0) current = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == null) {
                players.set(i, player);
                LOCK.unlock();
                return;
            }
        }
        players.add(player);
        LOCK.unlock();
    }

    public synchronized String getMark(Player player) {
        if (!players.contains(player)) return "ERROR";

        return marks.get(players.indexOf(player));
    }

    private String boardAsText() {
        StringBuilder text = new StringBuilder();
        for (var i = 0; i < boardWidth; i++) {
            for (var j = 0; j < boardWidth; j++) {
                text.append(board.get(i * boardWidth + j).getMark());
                if (j < boardWidth - 1)
                    text.append("|");
            }
            text.append("\n");
            if (i < boardWidth - 1)
                text.append("-------\n");
        }
        return text.toString();
    }


    public void handleInput(Player player, Packet packet) {
        LOCK.lock();
        switch (packet.getCode()) {
            case BOARD_UPDATE -> sendToPlayer(player, new Packet.PacketBuilder()
                    .code(Packet.Codes.BOARD_UPDATE).board(boardAsText()).build());
            case PLAYER_MOVE -> move(player, packet.getValue());
        }
        LOCK.unlock();
    }

    private void move(Player player, int position) {
        System.out.println(getMark(player) + " " + players.indexOf(player));
        if (players.indexOf(player) != current) {
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.OPPONENT_TURN).build());
            return;
        }
        if (board.get(position).getMark().equals(" ")) {
            board.get(position).setMark(getMark(player));
            if (hasWinner())
                for (int i = 0; i < maxPlayers; i++)
                    if (i != current)
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.GAME_END).message("you lost!").build());
                    else
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.GAME_END).message("you won!").build());
            else if (isFilledUp()) {
                for (int i = 0; i < maxPlayers; i++)
                    sendToPlayer(players.get(i), new Packet.PacketBuilder()
                            .code(Packet.Codes.GAME_END).message("Tie!").build());
            } else
                for (int i = 0; i < maxPlayers; i++)
                    if (i != current)
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.OPPONENT_MOVE).board(boardAsText())
                                .message("Opponent " + getMark(player) + " moved").build());
                    else
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.ACTION_SUCCESS).board(boardAsText()).build());
        } else {
            player.send(new Packet.PacketBuilder()
                    .code(Packet.Codes.ACTION_FAILURE).message("This field is already set").build());
        }
        current = (current + 1) % maxPlayers;
        sendToPlayer(players.get(current), new Packet.PacketBuilder().code(Packet.Codes.PLAYER_TURN).build());
    }

    private void sendToPlayer(Player player, Packet packet) {
        if (player == null) return;
        new Thread(() -> player.send(packet)).start();
    }

    private boolean hasWinner() {
        for (int i = 0; i < maxPlayers; i++) {
            int finalI = i;
            if (board.stream().filter(v -> v.getMark().equals(marks.get(finalI))).count() > 3) {
                return true;
            }
        }
        return false;
    }

    private boolean isFilledUp() {
        return board.stream().noneMatch(v -> v.getMark().equals(" "));
    }
}
