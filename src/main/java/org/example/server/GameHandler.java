package org.example.server;

import org.example.connection.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

//TODO move handler (this) to server class, requires proper implementation of handler, field and game classes first
public class GameHandler {

    //private final List<Field> board = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final List<String> marks = List.of("x", "o", "#");
    private final Game game;
    private final String gameVersion;
    private final Server server;
    private final ReentrantLock LOCK = new ReentrantLock();
    private int current;

    public GameHandler(String gameVersion, Server server, Game game) {
        this.server = server;
        this.gameVersion = gameVersion;
        this.game = game;
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

    public String boardAsText() {
        StringBuilder text = new StringBuilder();
        game.setMarks();
        for (var i = 0; i < game.getBoardHeight(); i++) {
            for (var j = 0; j < game.getBoardWidth(); j++) {
                text.append(game.getMark(j, i));
                if (j < game.getBoardWidth() - 1)
                    text.append("|");
            }
            text.append("\n");
            if (i < game.getBoardWidth() - 1)
                text.append("-------\n");
        }
        return text.toString();
    }


    public void handleInput(Player player, Packet packet) {
        LOCK.lock();
        switch (packet.getCode()) {
            case BOARD_UPDATE -> sendToPlayer(player, new Packet.PacketBuilder()
                    .code(Packet.Codes.BOARD_UPDATE).board(boardAsText()).build());
            case PLAYER_MOVE -> move(player, players.indexOf(player), 0, packet.getEnd().x, packet.getEnd().y);
        }
        LOCK.unlock();
    }

    private void move(Player player, int x0, int y0, int x1, int y1) {
        System.out.println(getMark(player) + " " + players.indexOf(player));
        if (players.indexOf(player) != current) {
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.OPPONENT_TURN).build());
            return;
        }
        if (game.isMoveLegal(x0, y0, x1, y1)) {
            if (game.hasWinner())
                for (int i = 0; i < game.getNumberOfPlayers(); i++)
                    if (i != current)
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.GAME_END).message("you lost!").build());
                    else
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.GAME_END).message("you won!").build());
            else if (game.isFilledUp()) {
                for (int i = 0; i < game.getNumberOfPlayers(); i++)
                    sendToPlayer(players.get(i), new Packet.PacketBuilder()
                            .code(Packet.Codes.GAME_END).message("Tie!").build());
            } else
                for (int i = 0; i < game.getNumberOfPlayers(); i++)
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
        current = (current + 1) % game.getNumberOfPlayers();
        sendToPlayer(players.get(current), new Packet.PacketBuilder().code(Packet.Codes.PLAYER_TURN).build());
    }

    private void sendToPlayer(Player player, Packet packet) {
        if (player == null) return;
        new Thread(() -> player.send(packet)).start();
    }

    /*
    private boolean hasWinner() {
        for (int i = 0; i < maxPlayers; i++) {
            int finalI = i;
            if (board.stream().filter(v -> v.getMark().equals(marks.get(finalI))).count() > 3) {
                return true;
            }
        }
        return false;
    }

     */

    /*
    private boolean isFilledUp() {
        return board.stream().noneMatch(v -> v.getMark().equals(" "));
    }

     */

    public Game getGame() { return game; }
}
