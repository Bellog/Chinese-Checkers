package org.example.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.example.connection.Packet;
import org.example.server.replay.GameSaveRepository;
import org.example.server.web.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Server class that acts as a mediator between gameHandler and ServerConnection classes.
 * <br> It also has logic around handling players' disconnections and reconnections.
 * <br> This class can handle a singe game at once and is tied to it (server closes when game ends).
 * <p></p> Main method inside uses cli to choose a game mode and starts server.
 */
@Component
@Profile("normal")
public class Server implements IServer {

    private final ReentrantLock LOCK = new ReentrantLock();

    protected GameSaveRepository repository;
    protected volatile WebSocketController conn;
    protected volatile IGameHandler gameHandler;
    /**
     * if player starts with 'null' then it is not assigned
     */
    protected volatile BiMap<Integer, String> playerMap;
    /**
     * If game is running then app_info code's handling should be different, see handlePacket() method for more information.
     */
    protected volatile boolean gameStarted = false;
    protected volatile boolean gameRunning = false;
    protected volatile boolean initialized = false;

    @Autowired
    public void setRepository(GameSaveRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setConn(WebSocketController conn) {
        this.conn = conn;
    }

    @Override
    public void setGameHandler(IGameHandler handler) {
        this.gameHandler = handler;
        playerMap = HashBiMap.create();
        for (int i = 0; i < handler.getNumberOfPlayers(); i++)
            playerMap.put(i, "null" + i);
        initialized = true;
    }

    @Override
    public void handlePacket(String playerId, Packet packet) {
        LOCK.lock();
        if (!initialized) return;
        switch (packet.getCode()) {
            case DISCONNECT -> {
                int player;
                try {
                    player = playerMap.inverse().get(playerId);
                } catch (NullPointerException e) {
                    // a client may disconnect before getting their id assigned that means that they were not
                    // added to the game by the server - no action is required
                    LOCK.unlock();
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
                if (addPlayer(playerId)) {
                    int player = playerMap.inverse().get(playerId);
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
        LOCK.unlock();
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
                gameHandler.gameStart();
                System.out.println("The game has started");
                sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                        .message("All players have joined, starting the game.").build());
            } else {
                System.out.println("The game has resumed");
                sendToAllExcept(player, new Packet.PacketBuilder().code(Packet.Codes.GAME_RESUME)
                        .message("All players have reconnected, resuming the game.").build());
                gameRunning = true;
            }
        } else {
            if (gameStarted) {
                sendToAllExcept(player, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                        .message("Player " + player + " has reconnected.").build());
                System.out.println("Player " + player + " has reconnected.");
            } else {
                sendToAllExcept(player, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                        .message("Player " + player + " has joined.").build());
                System.out.println("Player " + player + " has joined.");
            }
        }

        return true;
    }

    @Override
    public void stop() {
        System.out.println("Stopping the server");
        new Thread(() -> {
            sendToAllExcept(-1, new Packet.PacketBuilder().code(Packet.Codes.GAME_END)
                    .message("The game has ended").build());
            gameRunning = false;
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


