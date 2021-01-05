package org.example.client;

import org.example.Pair;
import org.example.connection.Packet;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class GamePanel extends JPanel {

    private final static Dimension fieldDim = new Dimension(28, 48);
    private final int diameter = Math.min(fieldDim.width, fieldDim.height);
    private final List<List<Field>> board = new ArrayList<>();
    private final int playerId;
    private final List<Color> colorScheme;
    private final ImageIcon boardBackground;
    // all fields are offset by a single field in x axis, see cropImage method
    private final MouseHandler handler = new MouseHandler(fieldDim, diameter, new Pair(fieldDim.width, 0)) {
        @Override
        protected void setFieldSelection(Pair pos, boolean selected) {
            board.get(pos.second).get(pos.first).setSelected(selected);
        }

        @Override
        protected boolean startCheck(Pair pos) {
            if (board.get(pos.second).get(pos.first) != null)
                return board.get(pos.second).get(pos.first).getState() == playerId;
            else
                return false;
        }

        @Override
        protected boolean endCheck(Pair pos) {
            return board.get(pos.second).get(pos.first) != null;
        }

        @Override
        public void send(Packet packet) {
            GamePanel.this.send(packet);
        }
    };

    GamePanel(int playerId, List<Color> colorScheme, List<List<Integer>> board, ImageIcon boardBackground) {
        this.playerId = playerId;
        this.colorScheme = colorScheme;
        this.boardBackground = cropImage(boardBackground);

        for (int y = 0; y < board.size(); y++) {
            this.board.add(new ArrayList<>());
            for (int x = 0; x < board.get(0).size(); x++) {
                if (board.get(y).get(x) == null) {
                    this.board.get(y).add(null);
                } else
                    this.board.get(y).add(new Field(board.get(y).get(x)));
            }
        }

        setVisible(true);
    }

    /**
     * When server send a board background it has border of 1 field in each dimension. this method remover
     * vertical border.
     *
     * @param imageIcon image to crop
     * @return cropped image
     */
    private ImageIcon cropImage(ImageIcon imageIcon) {
        BufferedImage temp = new BufferedImage(imageIcon.getIconWidth(),
                imageIcon.getIconHeight(), BufferedImage.TYPE_3BYTE_BGR);
        imageIcon.paintIcon(null, temp.getGraphics(), 0, 0);

        return new ImageIcon(temp.getSubimage(0, fieldDim.height, imageIcon.getIconWidth(),
                imageIcon.getIconHeight() - 2 * fieldDim.height));
    }

    public void setStatus(boolean active) {
        // reset selection of all fields
        board.forEach(row -> row.stream().filter(Objects::nonNull).forEach(f -> f.setSelected(false)));
        if (active) {
            // make sure that there is no handler is not there (i.e when this method is called twice with active = true
            removeMouseListener(handler);
            addMouseListener(handler);
        } else {
            removeMouseListener(handler);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(boardBackground.getIconWidth(), boardBackground.getIconHeight());
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

    public abstract void send(Packet packet);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        boardBackground.paintIcon(null, g, 0, 0);

        var g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int y = 0; y < board.size(); y++) {
            for (int x = 0; x < board.get(0).size(); x++) {
                if (board.get(y).get(x) == null)
                    continue;

                if (board.get(y).get(x).getState() >= 0)
                    g2d.setColor(colorScheme.get(board.get(y).get(x).getState()));
                else
                    g2d.setColor(Color.WHITE);

                g2d.fillOval((x + 1) * fieldDim.width,
                        y * fieldDim.height + (fieldDim.height - diameter) / 2, diameter, diameter);

                g2d.setColor(Color.BLACK);
                if (board.get(y).get(x).isSelected())
                    g2d.setStroke(new BasicStroke(6));
                else
                    g2d.setStroke(new BasicStroke(2));
                g2d.drawOval((x + 1) * fieldDim.width,
                        y * fieldDim.height + (fieldDim.height - diameter) / 2, diameter, diameter);
            }
        }
    }

}
