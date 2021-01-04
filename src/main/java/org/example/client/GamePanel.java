package org.example.client;

import org.example.Pair;
import org.example.connection.Packet;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    private final static Dimension fieldDim = new Dimension(28, 48);
    private final int diameter = Math.min(fieldDim.width, fieldDim.height);
    private final List<List<Field>> board = new ArrayList<>();
    private final int playerId;
    private final List<Color> colorScheme;
    private final ImageIcon boardBackground;
    private final IClient client;

    GamePanel(int playerId, List<Color> colorScheme, List<List<Integer>> board, IClient client, ImageIcon boardBackground) {
        this.playerId = playerId;
        this.colorScheme = colorScheme;
        this.client = client;
        this.boardBackground = boardBackground;

        for (int y = 0; y < board.size(); y++) {
            this.board.add(new ArrayList<>());
            for (int x = 0; x < board.get(0).size(); x++) {
                if (board.get(y).get(x) == null) {
                    this.board.get(y).add(null);
                } else
                    this.board.get(y).add(new Field(board.get(y).get(x)));
            }
        }

        initMouseListener();
        setVisible(true);
    }

    private void initMouseListener() {
        addMouseListener(new MouseHandler(fieldDim, diameter) {
            @Override
            protected boolean startCheck(Pair pos) {
                return board.get(pos.second).get(pos.first).getState() == playerId;
            }

            @Override
            protected boolean endCheck(Pair pos) {
                return board.get(pos.second).get(pos.first) != null;
            }

            @Override
            public void send(Packet packet) {
                client.send(packet);
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(board.get(0).size() * fieldDim.width, board.size() * fieldDim.height);
    }

    void update(List<List<Integer>> board) {
        for (int y = 0; y < board.size(); y++) {
            for (int x = 0; x < board.get(0).size(); x++) {
                if (board.get(y).get(x) == null)
                    continue;
                this.board.get(y).get(x).setState(board.get(y).get(x));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(boardBackground.getImage(), 0, 0, boardBackground.getIconWidth(), boardBackground.getIconHeight(), null);
        for (int y = 0; y < board.size(); y++) {
            for (int x = 0; x < board.get(0).size(); x++) {
                if (board.get(y).get(x) == null)
                    continue;

                if (board.get(y).get(x).getState() >= 0)
                    g2d.setColor(colorScheme.get(board.get(y).get(x).getState()));
                else
                    g2d.setColor(Color.WHITE);

                g2d.fillOval(x * fieldDim.width, y * fieldDim.height + (fieldDim.height - diameter) / 2, diameter, diameter);

                g2d.setColor(Color.BLACK);
                if (board.get(y).get(x).isSelected())
                    g2d.setStroke(new BasicStroke(4));
                else
                    g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x * fieldDim.width, y * fieldDim.height + (fieldDim.height - diameter) / 2, diameter, diameter);
            }
        }
    }

}
