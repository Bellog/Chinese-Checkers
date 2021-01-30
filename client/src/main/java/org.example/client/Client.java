package org.example.client;

import org.example.connection.Packet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/*
    This class holds client's logic (i.e which frame to show, etc.)
 */
@org.springframework.stereotype.Component
public class Client implements IClient {

    private final IClientConnection conn;
    private AppFrame frame;
    private GamePanel gamePanel;
    private SidePanel sidePanel;

    @Autowired
    public Client(IClientConnection conn) {
        this.conn = conn;
    }

    public void init() {
        try {
            System.out.println("Initializing connection");
            conn.init(GamePanel.fieldDim, this);
        } catch (Exception e) {
            System.out.println("Failed to initialize connection, closing program");
            System.exit(0);
        }
    }

    /**
     * Initializes GUI
     *
     * @param colorScheme     not null
     * @param playerId        not null
     * @param board           not null
     * @param boardBackground not null
     * @param playerInfo      not null
     */
    private void startGame(List<Color> colorScheme, int playerId, List<List<Integer>> board, ImageIcon boardBackground, List<List<String>> playerInfo) {
        if (frame != null || gamePanel != null || sidePanel != null)
            return; //if either of these is not null, then start game was already called

        frame = new AppFrame();
        gamePanel = new GamePanel(playerId, colorScheme, board, boardBackground) {
            @Override
            public void send(Packet packet) {
                conn.send(packet);
            }
        };
        sidePanel = new SidePanel(playerInfo, colorScheme, gamePanel.getPreferredSize().height) {
            @Override
            public void send(Packet packet) {
                conn.send(packet);
            }
        };

        frame.getContentPane().add(gamePanel);
        Component c = Box.createHorizontalStrut(5);
        c.setBackground(Color.BLACK);
        c.setForeground(Color.BLACK);
        frame.getContentPane().add(c);
        frame.getContentPane().add(sidePanel);

        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void setPanelStatus(boolean status) {
        if (gamePanel != null)
            gamePanel.setStatus(status);
        if (sidePanel != null)
            sidePanel.setStatus(status);
    }

    /**
     * Logs to gui, if unavailable, falls back to stdout
     *
     * @param message message to show
     */
    private void log(String message) {
        if (message != null) {
            if (sidePanel != null)
                sidePanel.updateLogs(message);
            else
                System.out.println(message);
        }
    }

    @Override
    public synchronized void handlePacket(Packet packet) {
        switch (packet.getCode()) {
            case GAME_SETUP -> startGame(packet.getColors(), packet.getPlayerId(),
                    packet.getBoard(), packet.getImage(), packet.getPlayerInfo());
            case TURN_START, GAME_RESUME -> setPanelStatus(true);
            case TURN_END, GAME_PAUSE, DISCONNECT, GAME_END -> setPanelStatus(false);
            case BOARD_UPDATE -> {
                if (gamePanel != null)
                    gamePanel.update(packet.getBoard());
            }
            case PLAYER_UPDATE -> {
                if (sidePanel != null)
                    sidePanel.updatePlayerInfo(packet.getPlayerInfo());
            }
        }

        log(packet.getMessage());
    }
}
