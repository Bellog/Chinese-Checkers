package org.example.connection;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;

public class Server {

    public static void main(String[] args) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            String version = model.getVersion();

            AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();
            try {
                serverSocket.set(new ServerSocket(ConnectionHelper.DEFAULT_PORT));
                serverSocket.get().setSoTimeout(10000);
            } catch (IOException ignored) {
            } finally {
                if (serverSocket.get() == null) {
                    System.out.println("Could not create server socket, closing program.");
                    System.exit(0);
                }
                System.out.println("Opened server.");
            }

            var game = new Game(version);
            game.addPlayer(new Player("x", serverSocket.get().accept(), game));
            game.addPlayer(new Player("o", serverSocket.get().accept(), game));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Server failed to start.");
        }
    }


}


