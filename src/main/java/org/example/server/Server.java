package org.example.server;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.example.connection.Packet;
import org.example.server.gameModes.AvailableGameModes;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Server class that acts as a mediator between gameHandler and ServerConnection classes.
 * <br> It also has logic around handling players' disconnections and reconnections.
 * <br> This class can handle a singe game at once and is tied to it (server closes when game ends).
 * <p></p> Main method inside uses cli to choose a game mode and starts server.
 */
public class Server implements IServer {

    private final IServerConnection conn;
    private final GameHandler gameHandler;
    private final String version;
    private final AtomicInteger disconnected = new AtomicInteger(0);
    private final List<Dimension> fieldDims = new ArrayList<>();

    /**
     * If game is running then app_info code's handling should be different, see handlePacket() method for more information.
     */
    private volatile boolean gameStarted = false;

    public Server(AvailableGameModes.GameModes mode, int maxPlayers) {
        String version = null;

        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            version = model.getVersion();
            if (version == null)
                throw new IOException();
        } catch (IOException | XmlPullParserException e) {
            System.out.println("Could not get program version");
            System.exit(1);
        }
        this.version = version;

        conn = new ServerConnection(this, maxPlayers);

        gameHandler = new GameHandler(mode, maxPlayers, this);
    }

    /**
     * Starts server and asks for server configuration using cli.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        System.out.println("Please choose one of available game modes:");
        for (int i = 0; i < AvailableGameModes.GameModes.values().length; i++) {
            System.out.println("\t" + i + ": " + AvailableGameModes.GameModes.values()[i].name());
        }
        AvailableGameModes.GameModes mode = null;
        int players = 0;
        try {
            mode = AvailableGameModes.GameModes.values()[sc.nextInt()];
            if (mode == null)
                throw new Exception();
            System.out.println("Please choose one of these player configuration for chosen game mode:\n\t" +
                               AvailableGameModes.getPlayerNumberList(mode));
            players = sc.nextInt();
            if (players <= 0)
                throw new Exception();
        } catch (Exception e) {
            System.out.println("Incorrect input");
            System.exit(1);
        }

        var s = new Server(mode, players);
        s.init();
    }

    /**
     * Initialized server and waits for all players, then starts the game
     */
    public void init() {
        new Thread(() -> {
            int i = 0;
            while (i < gameHandler.getNumberOfPlayers()) {
                if (conn.addPlayer()) {
                    fieldDims.add(null);
                    i++;
                }
            }

            System.out.println("Found all players");

            int wait = 0;
            // waits 50ms then checks if all players have sent their fieldDims
            while (wait < 100) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (fieldDims.stream().anyMatch(Objects::isNull)) {
                    wait++;
                } else
                    break;
            }
            if (wait == 100) {
                System.out.println("Could not get data from client, closing the program");
                System.exit(1);
            }

            gameHandler.gameStart(fieldDims);
            gameStarted = true;
        }).start();
    }

    /**
     * Adds player with supplied playerId, and notifies players if it was unsuccessful.
     *
     * @param player playerId
     */
    private void addPlayer(int player) {
        new Thread(() -> {
            if (conn.addPlayer()) {
                disconnected.decrementAndGet();
                resumeGame(player);
            } else {
                for (int i = 0; i < gameHandler.getNumberOfPlayers(); i++)
                    conn.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                            .message("Could not find a new player, the game will not resume").build());
            }
        }).start();
    }

    @Override
    public synchronized void handlePacket(int player, Packet packet) {
        System.out.println(player + " : " + packet.getCode().name());
        switch (packet.getCode()) {
            case CONNECTION_LOST -> {
                System.out.println("Lost connection to player " + player + ", pausing the game");
                disconnected.incrementAndGet();
                gameHandler.handleInput(player, new Packet.PacketBuilder().code(Packet.Codes.TURN_ROLLBACK).build());
                for (int i = 0; i < gameHandler.getNumberOfPlayers(); i++)
                    conn.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.GAME_RESUME)
                            .message("Player " + player + " disconnected, pausing the game.").build());
                addPlayer(player);
            }
            case APP_INFO -> {
                if (gameStarted)
                    gameHandler.joinPlayer(player, packet.getFieldDim());
                else
                    fieldDims.set(player, packet.getFieldDim());
            }
            default -> {
                if (disconnected.get() == 0)
                    gameHandler.handleInput(player, packet);
            }
        }
    }

    @Override
    public void sendToPlayer(int playerId, Packet packet) {
        conn.sendToPlayer(playerId, packet);
    }

    @Override
    public void stop() {
        System.out.println("Stopping the server");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
    }

    /**
     * Handles game resuming, should be used when a player reconnects
     *
     * @param player reconnected player's id
     */
    private void resumeGame(int player) {
        for (int i = 0; i < gameHandler.getNumberOfPlayers(); i++)
            conn.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("Player " + player + " has reconnected.").build());

        if (disconnected.get() == 0) {
            for (int i = 0; i < gameHandler.getNumberOfPlayers(); i++)
                conn.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.GAME_RESUME)
                        .message("All players have reconnected, resuming the game.").build());
        }
    }

    @Override
    public String getVersion() {
        return version;
    }
}


