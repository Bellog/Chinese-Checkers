package org.example.client;

import org.example.Pair;
import org.example.connection.Packet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class Client extends JFrame implements IClient {

    private final List<List<Field>> board = new ArrayList<>();
    private final ClientConnection conn;
    private List<Color> colors;

    /**
     * Class constructor.
     */
    public Client() {
        super("Sternhalma");

        conn = new ClientConnection(this);

        while (!conn.isInitialized()) {
            try {
                Thread.sleep(100); //ignore this warning for now
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setAlwaysOnTop(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 800));
        setMinimumSize(new Dimension(800, 800));
        setMaximumSize(new Dimension(800, 800));
        getContentPane().setBackground(Color.WHITE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        setupBoard();

        setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("Starting client");
        new Client();
    }

    @Override
    public List<Color> getColors() {
        return colors;
    }

    @Override
    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    private void setupBoard() {

    }

    @Override
    public void setPlayerInfo(int value) {
        setTitle("Sternhalma \"" + value + "\"");
    }

    @Override
    public void update(List<List<Pair>> board) {
        if (this.board.isEmpty()) {
            getContentPane().setLayout(new GridLayout(board.size(), board.get(0).size()));
            MouseListener m;
            m = new MouseAdapter() {
                private Pair first = null;

                @Override
                public void mouseClicked(MouseEvent e) {
                    Pair pos = ((Field) e.getSource()).getPosition();
                    if (first == null) {
                        first = pos;
                    } else {
                        if (!pos.equals(first))
                            conn.send(new Packet.PacketBuilder().code(Packet.Codes.PLAYER_MOVE)
                                    .start(first).end(((Field) e.getSource()).getPosition()).build());
                        first = null;
                    }
                }
            };
            for (int y = 0; y < board.size(); y++) {
                this.board.add(new ArrayList<>());
                for (int x = 0; x < board.get(0).size(); x++) {
                    var field = new Field(board.get(y).get(x).first, board.get(y).get(x).second,
                            new Pair(x, y), this);
                    field.addMouseListener(m);
                    this.board.get(y).add(field);
                    getContentPane().add(field);
                }
            }
            repaint();
        } else {
            for (int y = 0; y < board.size(); y++) {
                for (int x = 0; x < board.get(0).size(); x++) {
                    this.board.get(y).get(x).setState(board.get(y).get(x).first);
                }
            }
        }
    }
}
