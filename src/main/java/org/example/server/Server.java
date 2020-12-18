package org.example.server;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.example.connection.ConnectionHelper;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;

public class Server {

    private final AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();
    private final Game game;
    /**
     * Maximum time server will wait for find a new player, in seconds.
     */
    private final int maxTimeout = 30;
    private int i = 0;

    public Server() {

        String version = null;
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            version = model.getVersion();

            try {
                serverSocket.set(new ServerSocket(ConnectionHelper.DEFAULT_PORT));
                serverSocket.get().setSoTimeout(maxTimeout * 1000);
            } catch (IOException ignored) {
            } finally {
                if (serverSocket.get() == null) {
                    System.out.println("Could not create server socket, closing program.");
                    System.exit(1);
                }
                System.out.println("Opened server.");
            }
        } catch (Exception e) {
            System.out.println("Server failed to start.");
            System.exit(2);
        }

        if (version == null) {
            System.out.println("Error getting program version");
            System.exit(3);
        }
        if (serverSocket.get() == null) {
            System.out.println("Failed to create server");
            System.exit(4);
        }
        game = new Game(version, this);

        int currentPlayers = 0;
        while (currentPlayers < game.getMaxPlayers()) {
            System.out.println("looking for " + (game.getMaxPlayers() - currentPlayers) + " more players");
            try {
                game.addPlayer(new Player(serverSocket.get().accept(), game, i++));
                currentPlayers++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Found all players");
    }

    public static void main(String[] args) {
        new Server();
    }

    public synchronized boolean getNewPlayer() {
        System.out.println("looking for a player");
        try {
            game.addPlayer(new Player(serverSocket.get().accept(), game, i++));
            System.out.println("Player found!");
            return true;
        } catch (IOException e) {
            System.out.println("looking for a player...");
        }
        return false;
    }
}


