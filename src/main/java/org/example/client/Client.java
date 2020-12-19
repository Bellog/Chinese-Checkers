package org.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class Client extends JFrame {

    private final List<Field> board = new ArrayList<>();
    private final ClientConnection conn;

    public Client() {
        super("Sternhalma");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 800));
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(new GridLayout(4, 4));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        setupBoard();

        conn = new ClientConnection(this);

        new Timer(100, event -> {
            if (conn.isInitialized()) {
                setVisible(true);
                ((Timer) event.getSource()).stop();
            }
        }).start();
    }

    public static void main(String[] args) {
        System.out.println("Starting client");
        new Client();
    }

    private void setupBoard() {
        MouseListener m = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Field field = (Field) e.getSource();
                conn.send(board.indexOf(field));
            }
        };

        for (int i = 0; i < 16; i++) {
            Field field = new Field();
            field.addMouseListener(m);
            board.add(field);
            getContentPane().add(field);
        }
        setSize(800, 800);
        setLocationRelativeTo(null);
    }

    public void setPlayerInfo(String text) {
        setTitle("Sternhalma \"" + text + "\"");
    }

    public void update(List<String> board) {
        for (int i = 0; i < 16; i++) {
            this.board.get(i).setMark(board.get(i));
            this.board.get(i).repaint();
        }
    }
}
