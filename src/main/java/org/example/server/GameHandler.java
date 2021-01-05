package org.example.server;

import org.example.Pair;
import org.example.connection.Packet;
import org.example.server.gameModes.AbstractGameMode;
import org.example.server.gameModes.BasicGameMode;
import org.example.server.gameModes.StandardGameMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

//TODO move handler (this) to server class, requires proper implementation of handler, field and game classes first
public class GameHandler {

    private final List<Player> players;
    private final AbstractGameMode game;
    private final String gameVersion;
    private final Server server;
    private final ReentrantLock LOCK = new ReentrantLock();
    private int currentPlayer;

    /**
     * Class constructor.
     *
     * @param gameVersion version needed to play the game.
     * @param server      given server.
     * @param mode        type of game that will be played.
     */
    public GameHandler(String gameVersion, Server server, AvailableGameModes.GameModes mode) {
        this.server = server;
        this.gameVersion = gameVersion;
        AvailableGameModes available = new AvailableGameModes();
        this.game = available.getGameMode(mode);
        players = new ArrayList<>(Collections.nCopies(game.getNumberOfPlayers(), null));
    }

    public void removePlayer(Player player) {
        LOCK.lock();
        int id = getPlayerId(player);
        players.set(players.indexOf(player), null);
        players.stream().filter(Objects::nonNull)
                .forEach(p -> sendToPlayer(p, new Packet.PacketBuilder()
                        .code(Packet.Codes.INFO).message("Lost connection to player " + id).build()));
        LOCK.unlock();

        new Thread(() -> {
            if (!server.getNewPlayer()) {   // blocking call
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

    public List<List<Integer>> boardAsList() {
        return game.getBoard();
    }

    public void gameStart() {
        for (int i = 0; i < players.size(); i++) {
            //TODO get dimension from player
            players.get(i).send(new Packet.PacketBuilder()
                    .code(Packet.Codes.GAME_START).colorScheme(game.getColorScheme())
                    .board(game.getBoard()).playerId(i)
                    .image(game.getBoardBackground(new Dimension(28, 48))).build());
        }
    }


    public void handleInput(Player player, Packet packet) {
        LOCK.lock();
        switch (packet.getCode()) {
            case BOARD_UPDATE -> sendToPlayer(player, new Packet.PacketBuilder()
                    .code(Packet.Codes.BOARD_UPDATE).board(boardAsList()).build());
            case PLAYER_MOVE -> move(player, packet.getStartPos(), packet.getEndPos());
            //case TURN_END -> ;
        }
        LOCK.unlock();
    }

    private void move(Player player, Pair p0, Pair p1) {
        System.out.println(players.indexOf(player) + ": (" + p0.first + ", " + p0.second + ") -> (" + p1.first + ", " + p1.second + ")");
        if (players.indexOf(player) != currentPlayer) {
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.OPPONENT_TURN).build());
            return;
        }
        if (game.getFieldInfo(p0.first, p0.second) != players.indexOf(player)) {
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.ACTION_FAILURE)
                    .message("This is not your pawn").build());
            return;
        }

        if (game.move(p0, p1)) {
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

    private void sendToPlayer(Player player, Packet packet) {
        if (player == null) return;
        new Thread(() -> player.send(packet)).start();
    }

    public AbstractGameMode getGame() {
        return game;
    }
}
