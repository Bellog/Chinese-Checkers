package org.example.server.gameModes;

import org.example.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

/**
 * Interface used to implement various game modes.
 */
public abstract class AbstractGameMode {
    public final Map<Integer, Map<Integer, Integer>> playerBases = new TreeMap();
    protected final List<List<Integer>> board = new ArrayList<>();
    protected final int maxPlayers;
    protected final List<List<Integer>> defaultBoard = getDefaultBoard();
    public List<Pair> tempMoveList = new ArrayList<>();

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
     * This method should use neighborCheck internally and not depend on current state of the game
     * (using getNeighbors(Pair) for every check achieves that
     * <ul>
     * Logic in this method determines which field can be neighbors, not whether they are valid neighbors
     * (i.e whether they are occupied), that should be implemented by getPossibleMoves(Pair) method
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
        if (pos.first + xOffset < defaultBoard.get(0).size() && pos.first + xOffset >= 0 &&
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
        var background = new BoardBackgroundGenerator(fieldDim);
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
    abstract public boolean move(Pair p0, Pair p1);

    public void endTurn() {
        tempMoveList.clear();
    }

    public void rollBack() {
        if (tempMoveList.isEmpty())
            return;
        Collections.swap(tempMoveList, tempMoveList.size() - 1, 0);
    }

    abstract public boolean hasWinner();

    /**
     * Returns list in certain order: no player, player0, player1, etc.
     * if field's state is x then it's corresponding color is at x+1
     *
     * @return list of colors.
     */
    abstract public List<Color> getColorScheme();

    /**
     * Graphical board background generator.
     */
    private final class BoardBackgroundGenerator {

        private final Dimension fieldDim;
        private final ImageIcon background;

        /**
         * Instantiates this class and automatically draws background based on information in board and
         *
         * @param fieldDim width and height of a single field in client's GUI
         */
        private BoardBackgroundGenerator(Dimension fieldDim) {
            this.fieldDim = fieldDim;

            var background = new BufferedImage(fieldDim.width * (defaultBoard.get(0).size() + 2),
                    fieldDim.height * (defaultBoard.size() + 2), BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = (Graphics2D) background.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, background.getWidth(), background.getHeight());

            //offsets graphics by border
            drawBackground(g);

            this.background = new ImageIcon(background);
        }

        private void drawBackground(Graphics2D g) {
            for (int y = 0; y < getDefaultBoard().size(); y++) {
                for (int x = 0; x < getDefaultBoard().get(0).size(); x++) {
                    if (getDefaultBoard().get(y).get(x) == null)
                        continue;
                    drawBackgroundFieldBase(new Pair(x, y), g, fieldDim);
                }
            }

            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(4));

            //draws lines between fields
            for (int y = 0; y < defaultBoard.size(); y++) {
                for (int x = 0; x < defaultBoard.get(0).size(); x++) {
                    if (defaultBoard.get(y).get(x) == null)
                        continue;
                    var neighs = getNeighbors(new Pair(x, y));
                    for (Pair p : neighs) {
                        if (p != null)
                            g.drawLine((x + 1) * fieldDim.width + fieldDim.width / 2,
                                    (y + 1) * fieldDim.height + fieldDim.height / 2,
                                    (p.first + 1) * fieldDim.width + fieldDim.width / 2,
                                    (p.second + 1) * fieldDim.height + fieldDim.height / 2);
                    }
                }
            }
        }

        /**
         * Draws backgrounds around a field if it is a base field
         *
         * @param pos      position of a field that may be a base field
         * @param g        graphics to draw on
         * @param fieldDim field dimension to calculate positions
         */
        private void drawBackgroundFieldBase(Pair pos, Graphics2D g, Dimension fieldDim) {
            if (defaultBoard.get(pos.second).get(pos.first) >= 0) { // field is a base
                List<Pair> neighs = getNeighbors(pos);
                for (int i = 0; i < neighs.size() - 1; i++) {
                    drawBackgroundFieldBaseTile(pos, neighs.get(i), neighs.get(i + 1), g, fieldDim);
                }
                if (neighs.size() > 2)  // triangle between pos, last item and first item
                    drawBackgroundFieldBaseTile(pos, neighs.get(neighs.size() - 1), neighs.get(0), g, fieldDim);
            }
        }

        /**
         * Draws background triangle between pos, neigh0, neigh1. Color is based on pos
         *
         * @param pos      position of 1st vertex
         * @param neigh0   position of 2nd vertex
         * @param neigh1   position of 3rd vertex
         * @param g        graphics to draw on
         * @param fieldDim field dimension to calculate positions
         */
        private void drawBackgroundFieldBaseTile(Pair pos, Pair neigh0, Pair neigh1, Graphics g, Dimension fieldDim) {
            if (neigh0 == null || neigh1 == null)
                return;

            int[] xs = {(pos.first + 1) * fieldDim.width + fieldDim.width / 2,
                    (neigh0.first + 1) * fieldDim.width + fieldDim.width / 2,
                    (neigh1.first + 1) * fieldDim.width + fieldDim.width / 2};
            int[] ys = {(pos.second + 1) * fieldDim.height + fieldDim.height / 2,
                    (neigh0.second + 1) * fieldDim.height + fieldDim.height / 2,
                    (neigh1.second + 1) * fieldDim.height + fieldDim.height / 2};

            g.setColor(getColorScheme().get(defaultBoard.get(pos.second).get(pos.first)));
            g.fillPolygon(xs, ys, 3);
        }
    }
}
