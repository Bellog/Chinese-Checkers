package org.example.server;

import org.example.connection.Packet;
import org.example.server.gameModes.AbstractGameMode;
import org.example.server.gameModes.BasicGameMode;

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
     * @param game        type of game that will be played.
     */
    public GameHandler(String gameVersion, Server server, BasicGameMode game) {
        this.server = server;
        this.gameVersion = gameVersion;
        this.game = game;
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
                    .image(game.getBoardBackground(new Dimension(28, 48)))
                    .playerInfo(generatePlayerInfo(i))
                    .build());
        }
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


    public void handleInput(Player player, Packet packet) {
        LOCK.lock();
        switch (packet.getCode()) {
            case BOARD_UPDATE -> sendToPlayer(player, new Packet.PacketBuilder()
                    .code(Packet.Codes.BOARD_UPDATE).board(boardAsList()).build());
            case TURN_MOVE -> move(player, packet.getStartPos().first, packet.getStartPos().second,
                    packet.getEndPos().first, packet.getEndPos().second);
        }
        LOCK.unlock();
    }

    private void move(Player player, int x0, int y0, int x1, int y1) {
        if (players.indexOf(player) != currentPlayer) {
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.OPPONENT_TURN).build());
            return;
        }
        if (game.getFieldInfo(x0, y0) != players.indexOf(player)) {
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
        //this is reworked in different commit
//        currentPlayer = (currentPlayer + 1) % game.getNumberOfPlayers();
//        sendToPlayer(players.get(currentPlayer), new Packet.PacketBuilder().code(Packet.Codes.TURN_START).build());
    }

    private void sendToPlayer(Player player, Packet packet) {
        if (player == null) return;
        new Thread(() -> player.send(packet)).start();
    }

    public AbstractGameMode getGame() {
        return game;
    }
}
