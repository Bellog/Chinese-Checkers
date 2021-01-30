package org.example.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.example.connection.Packet;
import org.example.server.replay.GameSaveRepository;
import org.example.server.web.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

/**
 * Server class that acts as a mediator between gameHandler and ServerConnection classes.
 * <br> It also has logic around handling players' disconnections and reconnections.
 * <br> This class can handle a singe game at once and is tied to it (server closes when game ends).
 * <p></p> Main method inside uses cli to choose a game mode and starts server.
 */
@Component
public class Server implements IServer {

    @Transient
    private final GameSaveRepository repository;
    private volatile WebSocketController conn;
    private volatile IGameHandler gameHandler;
    /**
     * if player starts with 'null' then it is not assigned
     */
    private volatile BiMap<Integer, String> playerMap;
    /**
     * If game is running then app_info code's handling should be different, see handlePacket() method for more information.
     */
    private volatile boolean gameStarted = false;
    private volatile boolean gameRunning = false;
    private volatile boolean initialized = false;

    @Autowired
    public Server(GameSaveRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setConn(WebSocketController conn) {
        this.conn = conn;
    }

    public void setGameHandler(IGameHandler handler) {
        this.gameHandler = handler;
        playerMap = HashBiMap.create();
        for (int i = 0; i < handler.getNumberOfPlayers(); i++)
            playerMap.put(i, "null" + i);
        initialized = true;
    }

    @Override
    public synchronized void handlePacket(String playerId, Packet packet) {
        if (!initialized) return;
        System.out.println("Packet");
        switch (packet.getCode()) {
            case DISCONNECT -> {
                int player;
                try {
                    player = playerMap.inverse().get(playerId);
                } catch (NullPointerException e) {
                    // a client may disconnect before getting their id assigned that means that they were not
                    // added to the game by the server - no action is required
                    return;
                }
                System.out.println("Lost connection to player " + player + ", pausing the game");
                playerMap.forcePut(player, "null" + player); // set player to null
                gameHandler.handleInput(player, new Packet.PacketBuilder().code(Packet.Codes.TURN_ROLLBACK).build());
                gameRunning = false;
                sendToAllExcept(player, new Packet.PacketBuilder().code(Packet.Codes.GAME_PAUSE)
                        .message("Player " + player + " disconnected, pausing the game.").build());
            }
            case CONNECT -> {
                System.out.println("new player");
                if (addPlayer(playerId)) {
                    int player = playerMap.inverse().get(playerId);
                    System.out.println("new player added as player " + player);
                    gameHandler.joinPlayer(player, packet.getFieldDim());
                } else {
                    System.out.println("could not add new player");
                }
            }
            default -> {
                int player = playerMap.inverse().get(playerId);
                System.out.println("player " + player + ": " + packet.getCode().name());
                if (gameRunning)
                    gameHandler.handleInput(player, packet);
            }
        }
    }

    @Override
    public void sendToPlayer(int player, Packet packet) {
        if (!playerMap.get(player).startsWith("null"))
            conn.sendToPlayer(playerMap.get(player), packet);
    }

    public void sendToAllExcept(int exceptPlayer, Packet packet) {
        for (int i = 0; i < gameHandler.getNumberOfPlayers(); i++)
            if (i != exceptPlayer)
                sendToPlayer(i, packet);
    }

    public void sendToAll(Packet packet) {
        for (int i = 0; i < gameHandler.getNumberOfPlayers(); i++)
            sendToPlayer(i, packet);
    }

    private boolean addPlayer(String playerId) {
        int player = -1;
        for (int i = 0; i < playerMap.size(); i++)
            if (playerMap.get(i).startsWith("null")) {
                player = i;
                break;
            }

        if (player == -1)
            return false;

        playerMap.forcePut(player, playerId);

        if (playerMap.inverse().keySet().stream().noneMatch(v -> v.startsWith("null"))) {
            if (!gameRunning) {
                gameStarted = true; // this will happen only once when all players are connected
                gameRunning = true;
                System.out.println("The game has started");
            } else {
                System.out.println("The game has resumed");
                gameRunning = true;
            }
        }

        if (!gameRunning)
            sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("The game is currently paused").build());

        if (gameStarted) {
            int finalPlayer = player;
            new Thread(() -> {
                sendToAllExcept(finalPlayer, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                        .message("Player " + finalPlayer + " has reconnected.").build());

                if (gameRunning) {
                    sendToAllExcept(finalPlayer, new Packet.PacketBuilder().code(Packet.Codes.GAME_RESUME)
                            .message("All players have reconnected, resuming the game.").build());
                }
            }).start();
        }

        return true;
    }

    @Override
    public void stop() {
        System.out.println("Stopping the server");
        new Thread(() -> {
            var save = gameHandler.getSave();
            System.out.println("SAVE: " + save.getMode().name() + " for " + save.getPlayers() + " players (" +
                               save.getMoves().size() + " moves)");
            repository.saveAndFlush(gameHandler.getSave());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }).start();
    }
}


