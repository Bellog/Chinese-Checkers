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
    private final AppFrame frame;
    private GamePanel gamePanel;

    public Client() {
        frame = new AppFrame();

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
    public void startGame(List<Color> colorScheme, int playerId, List<List<Integer>> board, ImageIcon boardBackground) {
        if (gamePanel != null)
            frame.remove(gamePanel);

        gamePanel = new GamePanel(playerId, colorScheme, board, this, boardBackground);

        //TODO send player color name in addition to playerId
        frame.setTitle("Sternhalma \"" + playerId + "\"");
        frame.getContentPane().add(gamePanel);
        frame.pack();
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
