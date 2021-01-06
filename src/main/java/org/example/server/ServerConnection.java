package org.example.server;

import org.example.connection.ConnectionHelper;
import org.example.connection.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ServerConnection implements IServerConnection {

    private final AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();
    private final List<IPlayerConnection> players = new ArrayList<>();
    private final IServer server;

    public ServerConnection(IServer server, int maxPlayers) {
        this.server = server;

        try {
            serverSocket.set(new ServerSocket(ConnectionHelper.DEFAULT_PORT));
            // Maximum time server will wait for find a new player, in seconds.
            serverSocket.get().setSoTimeout(60 * 1000);
        } catch (IOException ignored) {
        } finally {
            if (serverSocket.get() == null) {
                System.out.println("Could not create server socket, closing program.");
                System.exit(1);
            }
            System.out.println("Opened server.");
        }

        if (serverSocket.get() == null) {
            System.out.println("Failed to create server");
            System.exit(1);
        }

        for (int i = 0; i < maxPlayers; i++)
            players.add(null);
    }

    /**
     * This method is blocking
     *
     * @return true if a new player was added, false otherwise
     */
    public boolean addPlayer() {
        int i = players.indexOf(null);
        if (i < 0) {
            System.out.println("Cannot accept any more players");
            return false;
        }

        System.out.println("looking for a player " + i);

        int count = 0;
        while (count < 5) { //breaks if a player joins or after 5 tries
            try {
                var p = new PlayerConnection(serverSocket.get().accept(), server.getVersion()) {
                    @Override
                    protected void handlePacket(Packet packet) {
                        if (packet.getCode() == Packet.Codes.CONNECTION_LOST) {
                            players.set(i, null);
                        }
                        server.handlePacket(i, packet);
                    }
                };

                if (!p.init()) {
                    count++;
                    continue;
                }

                players.set(i, p);

                System.out.println("Player " + i + " found!");
                return true;
            } catch (IOException e) {
                System.out.println("looking for a player " + i + "...");
            }
        }
        return false;
    }

    @Override
    public void sendToPlayer(int player, Packet packet) {
        if (player >= 0 && player < players.size() && players.get(player) != null)
            players.get(player).send(packet);
    }
}
