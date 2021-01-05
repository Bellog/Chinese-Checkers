package org.example.server;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.example.connection.Packet;
import org.example.server.gameModes.AvailableGameModes;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements IServer {

    private final IServerConnection conn;
    private final GameHandler gameHandler;
    private final String version;
    private final AtomicInteger disconnected = new AtomicInteger(0);

    public Server() {
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

        int maxPlayers = 2;

        //TODO get maxPlayer from cli
        conn = new ServerConnection(this, maxPlayers);

        int i = 0;
        while (i < maxPlayers) {
            if (conn.addPlayer()) {
                i++;
            }
        }

        //TODO get game mode and maxPlayer from cli
        var gameMode = AvailableGameModes.getGameMode(AvailableGameModes.GameModes.STANDARD, maxPlayers);

        if (gameMode == null) {
            System.out.println("Cannot instantiate this game mode");
            System.exit(1);
        }

        gameHandler = new GameHandler(gameMode) {
            @Override
            protected void sendToPlayer(int player, Packet packet) {
                conn.sendToPlayer(player, packet);
            }
        };

        System.out.println("Found all players");
        gameHandler.gameStart();
    }

    public static void main(String[] args) {
        new Server();
    }

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
        if (packet.getCode() == Packet.Codes.CONNECTION_LOST) {
            System.out.println("Lost connection to player " + player + ", pausing the game");
            disconnected.incrementAndGet();
            for (int i = 0; i < gameHandler.getNumberOfPlayers(); i++)
                conn.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.GAME_RESUME)
                        .message("Player" + player + " disconnected, pausing the game.").build());
            addPlayer(player);
            return;
        }

        if (disconnected.get() == 0)
            gameHandler.handleInput(player, packet);
    }

    private void resumeGame(int player) {
        if (disconnected.get() > 0)
            return;

        for (int i = 0; i < gameHandler.getNumberOfPlayers(); i++)
            conn.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.GAME_RESUME)
                    .message("Player" + player + " reconnected, resuming the game.").build());
    }

    @Override
    public String getVersion() {
        return version;
    }
}


