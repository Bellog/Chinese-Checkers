package org.example.server.gameModes;

import org.example.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface used to implement various game modes.
 */
public abstract class AbstractGameMode {
    protected final List<List<Integer>> board = new ArrayList<>();
    protected final int maxPlayers;
    protected final List<List<Integer>> defaultBoard = getDefaultBoard();

    /**
     * Every concrete child should implement logic based on maxPlayers
     *
     * @param maxPlayers maximum number of players in the game
     */
    protected AbstractGameMode(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    protected abstract List<List<Integer>> getDefaultBoard();

    /**
     * Returns list of neighbors of a field at pos position.
     * <br>This method can used for drawing purposes or as a helper function for getPossibleMovesMethod(Pair) method,
     * It should not define rules for pawn movements.
     * <p></p>
     * This method should use neighborCheck internally.
     * <ul>
     * Logic in this method determines which field can be neighbors, not whether they are valid neighbors.
     * <br><br>
     * If a neighbor is not valid, it should be a null, so that this method returns arrays of constant size.</ul>
     *
     * @param pos position (x, y) of a field to check its neighbors
     * @return Returns list of (nullable )neighbors of a field at pos position
     */
    protected abstract List<Pair> getNeighbors(Pair pos);

    /**
     * Helper method for getNeighbors(Pair) method.
     *
     * @param pos     position of a field to check
     * @param xOffset checks neighbor at pos.first + xOffset
     * @param yOffset checks neighbor at pos.second + xOffset
     * @return Returns new Pair with neighbor position or null if neighbors is null
     */
    protected final Pair neighborCheck(Pair pos, int xOffset, int yOffset) {
        // field at pos + offset is withing bounds and is not null
        if (pos.first + xOffset < board.get(0).size() && pos.first + xOffset >= 0 &&
            pos.second + yOffset < board.size() && pos.second + yOffset >= 0 &&
            board.get(pos.second + yOffset).get(pos.first + xOffset) != null)
            return new Pair(pos.first + xOffset, pos.second + yOffset);
        else
            return null;
    }

    /**
     * List of field where pawn at pos can move.
     * <p></p>
     * This method should be aware of pawn's last movements
     *
     * @param pos position of a pawn to check
     * @return List of field where player can move, null if there is no pawn at specified position
     */
    public abstract List<Pair> getPossibleMoves(Pair pos);

    /**
     * Returns game board, where each field is a Pair of (state, type)
     * <p></p>
     * type - base of which player (-1 means it is nobody's base, >= 0 means it is base of a player with this id
     * <p></p>
     * state - which player has a (-1 means it is nobody's base, >= 0 means it is base of a player with this id
     *
     * @return game board
     */
    public final List<List<Integer>> getBoard() {
        return board;
    }

    /**
     * Returns winner's id, -1 if there is no winner
     *
     * @return winner's id, -1 if there is no winner
     */
    public abstract int winnerId();

    public final ImageIcon getBoardBackground(Dimension fieldDim) {
        var background = new BoardBackgroundGenerator(fieldDim, defaultBoard);
        return background.background;
    }

    abstract public int getNumberOfPlayers();

    /**
     * Used to access information about state and type of fields on board.
     *
     * @param x coordinate
     * @param y coordinate
     * @return state and type of a field, null if no such coordinates were found.
     */
    abstract public Integer getFieldInfo(int x, int y);

    /**
     * Moves pawn from (x0, y0) to (x1, y1).
     * Assumes that it's pawn owner's turn
     * Checks if move is possible.
     *
     * @return true if move is successful, false if not
     */
    abstract public boolean move(int x0, int y0, int x1, int y1);

    abstract public boolean hasWinner();

    /**
     * Returns list in certain order: no player, player0, player1, etc.
     * if field's state is x then it's corresponding color is at x+1
     *
     * @return list of colors.
     */
    abstract public List<Color> getColorScheme();

    //TODO make sure that this method is truly generic -> background is correct regardless of fieldDim proportions
    // currently it may assume that height > widths
    // USE defaultBoard instead
    private final class BoardBackgroundGenerator {

        private final Dimension fieldDim;
        private final List<List<Integer>> board;
        private ImageIcon background;

        private BoardBackgroundGenerator(Dimension fieldDim, List<List<Integer>> board) {
            this.board = board;
            this.fieldDim = fieldDim;
            drawBackground();
        }

        private void drawBackground() {
            var background = new BufferedImage(fieldDim.width * board.get(0).size(),
                    fieldDim.height * board.size(), BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = (Graphics2D) background.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, fieldDim.width * board.get(0).size(), fieldDim.height * board.size());

            g.setStroke(new BasicStroke(4));
            g.setColor(Color.BLACK);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int y = 0; y < board.size(); y++) {
                for (int x = 0; x < board.get(0).size(); x++) {
                    if (board.get(y).get(x) == null)
                        continue;
                    var neighs = getNeighbors(new Pair(x, y));
                    drawBackgroundFieldBase(new Pair(x, y), neighs, g, fieldDim);
                }
            }
            for (int y = 0; y < board.size(); y++) {
                for (int x = 0; x < board.get(0).size(); x++) {
                    if (board.get(y).get(x) == null)
                        continue;
                    var neighs = getNeighbors(new Pair(x, y));
                    for (Pair p : neighs) {
                        if (p != null)
                            g.drawLine(x * fieldDim.width + fieldDim.width / 2,
                                    y * fieldDim.height + fieldDim.height / 2,
                                    p.first * fieldDim.width + fieldDim.width / 2,
                                    p.second * fieldDim.height + fieldDim.height / 2);
                    }
                }
            }
            this.background = new ImageIcon(background);
        }

        private void drawBackgroundFieldBase(Pair pos, List<Pair> neighs, Graphics2D g, Dimension fieldDim) {
            if (board.get(pos.second).get(pos.first) >= 0) { // field is a base
                var gg = g.create();

                for (int i = 0; i < neighs.size() - 1; i++) {

                    drawBackgroundFieldBaseTile(pos, neighs.get(i), neighs.get(i + 1), gg, fieldDim);
                }
                if (neighs.size() > 2)  // n,0 wont happen in loop above if n > 1
                    drawBackgroundFieldBaseTile(pos, neighs.get(neighs.size() - 1), neighs.get(0), gg, fieldDim);
                gg.dispose();
            }
        }

        private void drawBackgroundFieldBaseTile(Pair pos, Pair neigh0, Pair neigh1, Graphics g, Dimension fieldDim) {
            if (neigh0 == null || neigh1 == null)
                return;

            int[] xs = {pos.first * fieldDim.width + fieldDim.width / 2,
                    neigh0.first * fieldDim.width + fieldDim.width / 2,
                    neigh1.first * fieldDim.width + fieldDim.width / 2};
            int[] ys = {pos.second * fieldDim.height + fieldDim.height / 2,
                    neigh0.second * fieldDim.height + fieldDim.height / 2,
                    neigh1.second * fieldDim.height + fieldDim.height / 2};
            g.setColor(getColorScheme().get(defaultBoard.get(pos.second).get(pos.first)));
            g.fillPolygon(xs, ys, 3);
        }
    }
}