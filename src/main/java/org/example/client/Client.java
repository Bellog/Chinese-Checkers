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

/**
 * Helps players access the game via server.
 * Has a main function that creates instance of this.
 */
public class Client extends JFrame implements IClient {

    /**
     * Show the state of the game.
     */
    private final List<List<Field>> board = new ArrayList<>();
    /**
     * Connection.
     */
    private final ClientConnection conn;
    /**
     * Used to determine one player's color.
     */
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

    /**
     * Main function, generates instance of Client.
     * @param args unused.
     */
    public static void main(String[] args) {
        System.out.println("Starting client");
        new Client();
    }

    /**
     * Color-list getter.
     * @return list of colors.
     */
    @Override
    public List<Color> getColors() {
        return colors;
    }

    /**
     * Color-list setter.
     * @param colors list of colors.
     */
    @Override
    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    private void setupBoard() {

    }

    /**
     * For a player to get information about themselfs in a game.
     * @param value number of a player.
     */
    @Override
    public void setPlayerInfo(int value) {
        setTitle("Sternhalma \"" + value + "\"");
    }

    /**
     * Generates graphics for players.
     * @param board representation of the board.
     */
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
