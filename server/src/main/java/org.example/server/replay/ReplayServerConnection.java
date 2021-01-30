package org.example.server.replay;

import org.example.connection.ConnectionHelper;
import org.example.connection.Packet;
import org.example.server.IPlayerConnection;
import org.example.server.IServer;
import org.example.server.IServerConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Server connection used for showing replays, connects to only 1 client, assumes that the clients' id is 0.
 */
public class ReplayServerConnection implements IServerConnection {
    private final AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();
    private final AtomicReference<IPlayerConnection> player = new AtomicReference<>();
    private final IServer server;

    public ReplayServerConnection(IServer server) {
        this.server = server;

        try {
            serverSocket.set(new ServerSocket(ConnectionHelper.DEFAULT_PORT));
            serverSocket.get().setSoTimeout(60 * 1000);
        } catch (IOException ignored) {
        } finally {
            if (serverSocket.get() == null) {
                System.out.println("Could not create server socket, closing program.");
                server.stop();
            }
            System.out.println("Opened server.");
        }
    }

    @Override
    public void sendToPlayer(int playerId, Packet packet) {
        if (playerId == -1)
            player.get().send(packet);
    }

    @Override
    public boolean addPlayer() {
//        if (player.get() != null) // ignore subsequent calls
//            return true;
//
//        int count = 0;
//        while (count < 5) { //breaks if a player joins or after 5 tries
//            try {
//                var p = new PlayerConnection(serverSocket.get().accept(), server.getVersion()) {
//                    @Override
//                    protected void handlePacket(Packet packet) {
//                        if (packet.getCode() == Packet.Codes.DISCONNECT) {
//                            player.set(null);
//                        }
//                        server.handlePacket(-1, packet);
//                    }
//                };
//
//                if (!p.init()) {
//                    count++;
//                    continue;
//                }
//
//                player.set(p);
//
//                System.out.println("Client found!");
//                return true;
//            } catch (IOException e) {
//                System.out.println("Waiting for client to start...");
//            }
//        }
//
//        if (player.get() == null) {
//            System.out.println("Could not find any clients");
//            return false;
//        }
        return false;
    }
}
