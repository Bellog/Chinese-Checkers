package org.example.client;

import org.example.connection.Packet;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/*
    This class holds client's logic (i.e which frame to show, etc.)
 */
public class Client implements IClient {

    private final IClientConnection conn;
    private AppFrame frame;
    private GamePanel gamePanel;
    private SidePanel sidePanel;

    public Client() {
        conn = new ClientConnection(this);
    }

    public static void main(String[] args) {
        System.out.println("Starting client");
        new Client();
    }

    public void startGame(List<Color> colorScheme, int playerId, List<List<Integer>> board, ImageIcon boardBackground, List<List<String>> playerInfo) {
        if (frame != null || gamePanel != null || sidePanel != null)
            return; //if either of these is not null, then start game was already called

        frame = new AppFrame();
        gamePanel = new GamePanel(playerId, colorScheme, board, boardBackground) {
            @Override
            public void send(Packet packet) {
                if (conn != null)
                    conn.send(packet);
            }
        };
        sidePanel = new SidePanel(playerInfo, colorScheme, gamePanel.getPreferredSize().height) {

            @Override
            public void send(Packet packet) {
                if (conn != null)
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

    @Override
    public synchronized void receive(Packet packet) {
        switch (packet.getCode()) {
            case GAME_START -> startGame(packet.getColorScheme(), packet.getPlayerId(),
                    packet.getBoard(), packet.getImage(), packet.getPlayerInfo());
            case TURN_START, GAME_RESUME -> setPanelStatus(true);
            case TURN_END, GAME_PAUSE, CONNECTION_LOST -> setPanelStatus(false);
            case BOARD_UPDATE -> {
                if (gamePanel != null)
                    gamePanel.update(packet.getBoard());
            }
            case PLAYER_UPDATE -> {
                if (sidePanel != null)
                    sidePanel.updatePlayerInfo(packet.getPlayerInfo());
            }
        }

        if (packet.getMessage() != null) {
            if (sidePanel != null)
                sidePanel.updateLogs(packet.getMessage());
            else
                System.out.println(packet.getMessage());
        }
    }
}
