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

        while (!conn.isInitialized()) {
            try {
                Thread.sleep(100); //ignore this warning for now
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting client");
        new Client();
    }

    @Override
    public void startGame(List<Color> colorScheme, int playerId, List<List<Integer>> board, ImageIcon boardBackground, List<List<String>> playerInfo) {
        frame = new AppFrame();
        gamePanel = new GamePanel(playerId, colorScheme, board, this, boardBackground);
        sidePanel = new SidePanel(playerInfo, this, colorScheme, gamePanel.getPreferredSize().height);

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

    @Override
    public void update(List<List<Integer>> board) {
        if (gamePanel != null)
            gamePanel.update(board);
    }

    @Override
    public void send(Packet packet) {
        if (conn.isInitialized())
            conn.send(packet);
    }
}
