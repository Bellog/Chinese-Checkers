package org.example.server;

import org.example.Pair;
import org.example.connection.Packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Operates players' turns and view of the game.
 */
//TODO move handler (this) to server class, requires proper implementation of handler, field and game classes first
public class GameHandler {

    //private final List<Field> board = new ArrayList<>();
    /**
     * Data about players.
     */
    private final List<Player> players;
    //    private final List<String> marks = List.of("x", "o", "#");
    /**
     * Type of game that will be played.
     */
    private final Game game;
    /**
     * PLayers need to have the same version as the server.
     */
    private final String gameVersion;
    /**
     * Game server.
     */
    private final Server server;
    /**
     * Safety of threads.
     */
    private final ReentrantLock LOCK = new ReentrantLock();
    /**
     * Index of current player in players
     */
    private int currentPlayer;

    /**
     * Class constructor.
     * @param gameVersion version needed to play the game.
     * @param server given server.
     * @param game type of game that will be played.
     */
    public GameHandler(String gameVersion, Server server, Game game) {
        this.server = server;
        this.gameVersion = gameVersion;
        this.game = game;
        players = new ArrayList<>(Collections.nCopies(game.getNumberOfPlayers(), null));
    }

    /**
     * Removes a player from the list.
     * @param player player that shall be removed from the game.
     */
    public void removePlayer(Player player) {
        LOCK.lock();
        int id = getPlayerId(player);
        players.set(players.indexOf(player), null);
        players.stream().filter(Objects::nonNull)
                .forEach(p -> sendToPlayer(p, new Packet.PacketBuilder()
                        .code(Packet.Codes.INFO).message("Lost connection to player " + id).build()));
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

    /**
     * Version getter.
     * @return version needed to play the game.
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * If a game is not full, adds a player that connected to it.
     * @param player a given player
     */
    public synchronized void addPlayer(Player player) {
        LOCK.lock();
        if (players.size() == 0) currentPlayer = 0;
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

    /**
     * Returns player's id
     *
     * @param player returns this players id
     * @return -1 if this player does not have any id
     */
    public synchronized int getPlayerId(Player player) {
        if (!players.contains(player)) return -1;

        return players.indexOf(player);
    }

    /**
     * Represents the situation of the game.
     * @return board representation.
     */
    public List<List<Pair>> boardAsList() {
        List<List<Pair>> board = new ArrayList<>();
        for (int y = 0; y < game.getBoardHeight(); y++) {
            board.add(new ArrayList<>());
            for (int x = 0; x < game.getBoardWidth(); x++) {
                board.get(y).add(game.getFieldInfo(x, y));
            }
        }
        return board;
    }


    /**
     * Interprets input from the player.
     * Sends signals to update the board or to move a player.
     * @param player a given player
     * @param packet packet of necessary data
     */
    public void handleInput(Player player, Packet packet) {
        LOCK.lock();
        switch (packet.getCode()) {
            case BOARD_UPDATE -> sendToPlayer(player, new Packet.PacketBuilder()
                    .code(Packet.Codes.BOARD_UPDATE).board(boardAsList()).build());
            case PLAYER_MOVE -> move(player, packet.getStartPos().first, packet.getStartPos().second,
                    packet.getEndPos().first, packet.getEndPos().second);
        }
        LOCK.unlock();
    }

    /**
     * Intends to move a player by given coordinates.
     * Checks if a win condition is fulfilled.
     * @param player the player
     * @param x0 x coordinate of a pawn that wants to move.
     * @param y0 y coordinate of a pawn that wants to move.
     * @param x1 x coordinate of the point that the pawn wants to be moved to.
     * @param y1 y coordinate of the point that the pawn wants to be moved to.
     */
    private void move(Player player, int x0, int y0, int x1, int y1) {
        System.out.println(players.indexOf(player) + ": (" + x0 + ", " + y0 + ") -> (" + x1 + ", " + y1 + ")");
        if (players.indexOf(player) != currentPlayer) {
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.OPPONENT_TURN).build());
            return;
        }
        if (game.getFieldInfo(x0, y0).first != players.indexOf(player)) {
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.ACTION_FAILURE)
                    .message("This is not your pawn").build());
            return;
        }

        if (game.move(x0, y0, x1, y1)) {
            if (game.hasWinner()) {
                for (int i = 0; i < game.getNumberOfPlayers(); i++)
                    if (i != currentPlayer)
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.GAME_END).message("you lost!").build());
                    else
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.GAME_END).message("you won!").build());
            } else
                for (int i = 0; i < game.getNumberOfPlayers(); i++)
                    if (i != currentPlayer)
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.OPPONENT_MOVE).board(boardAsList())
                                .message("Opponent " + getPlayerId(player) + " moved").build());
                    else
                        sendToPlayer(players.get(i), new Packet.PacketBuilder()
                                .code(Packet.Codes.ACTION_SUCCESS).board(boardAsList()).build());
        } else {
            player.send(new Packet.PacketBuilder()
                    .code(Packet.Codes.ACTION_FAILURE).message("This field is already set").build());
            return;
        }
        //move to next player and notify them
        currentPlayer = (currentPlayer + 1) % game.getNumberOfPlayers();
        sendToPlayer(players.get(currentPlayer), new Packet.PacketBuilder().code(Packet.Codes.PLAYER_TURN).build());
    }

    /**
     * Distributes important game information to a player.
     * @param player a given player.
     * @param packet data.
     */
    private void sendToPlayer(Player player, Packet packet) {
        if (player == null) return;
        new Thread(() -> player.send(packet)).start();
    }

    /**
     * Game getter.
     * @return type of played game.
     */
    public Game getGame() {
        return game;
    }
}
