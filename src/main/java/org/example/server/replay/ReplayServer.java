package org.example.server.replay;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.example.connection.Packet;
import org.example.server.IServer;
import org.example.server.IServerConnection;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ReplayServer implements IServer {

    private final String version;
    private final AtomicReference<Dimension> fieldDims = new AtomicReference<>(null);
    private volatile IServerConnection conn;
    private volatile org.example.server.IGameHandler gameHandler;

    public ReplayServer() {
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
    }

    @Override
    public void handlePacket(int playerId, Packet packet) {
        switch (packet.getCode()) {
            case CONNECTION_LOST -> {
                System.out.println("Lost connection to the client");
                stop();
            }
            case APP_INFO -> {
                System.out.println("XDD");
                if (fieldDims.get() == null) {
                    fieldDims.set(packet.getFieldDim());
                    gameHandler.gameStart(List.of(packet.getFieldDim()));
                }
            }
            default -> gameHandler.handleInput(playerId, packet);
        }
    }

    @Override
    public void sendToPlayer(int playerId, Packet packet) {
        if (playerId == -1)
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

    public void init(org.example.server.IGameHandler handler, IServerConnection conn) {
        this.gameHandler = handler;
        this.conn = conn;

        conn.addPlayer();
    }

    @Override
    public String getVersion() {
        return version;
    }
}
