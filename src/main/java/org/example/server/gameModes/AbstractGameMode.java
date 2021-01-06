package org.example.server.gameModes;

import org.example.Pos;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface used to implement various game modes.
 */
public abstract class AbstractGameMode {
    protected final List<List<Integer>> board;
    protected final int maxPlayers;
    protected final List<List<Integer>> defaultBoard;
    protected final Map<Integer, Map<Integer, Integer>> playerMap;
    protected final List<List<Pos>> winCondition;
    protected final List<Color> colorScheme;
    protected final List<Integer> winners;
    public List<Pos> tempMoveList = new ArrayList<>();

    protected AbstractGameMode(int maxPlayers, List<Color> colorScheme) {
        //order of some instantiation of these fields may be important
        // Refer to descriptions of individual methods when you add or rearrange these assignments
        this.maxPlayers = maxPlayers;
        this.colorScheme = colorScheme;
        winners = new ArrayList<>();
        defaultBoard = getDefaultBoard();
        playerMap = getPlayerMap();
        board = getStartingBoard();
        winCondition = getWinCondition();
        for (int i = 0; i < maxPlayers; i++)
            winners.add(null);
    }

    protected abstract Map<Integer, Map<Integer, Integer>> getPlayerMap();

    protected abstract List<List<Pos>> getWinCondition();

    protected abstract List<List<Integer>> getStartingBoard();

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
    protected abstract List<Pos> getNeighbors(Pos pos);

    /**
     * Helper method for getNeighbors(Pair) method.
     *
     * @param pos     position of a field to check
     * @param xOffset checks neighbor at pos.first + xOffset
     * @param yOffset checks neighbor at pos.second + xOffset
     * @return Returns new Pair with neighbor position or null if neighbors is null
     */
    protected final Pos neighborCheck(Pos pos, int xOffset, int yOffset) {
        // field at pos + offset is withing bounds and is not null
        if (pos.x + xOffset < defaultBoard.get(0).size() && pos.x + xOffset >= 0 &&
            pos.y + yOffset < board.size() && pos.y + yOffset >= 0 &&
            board.get(pos.y + yOffset).get(pos.x + xOffset) != null)
            return new Pos(pos.x + xOffset, pos.y + yOffset);
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
    protected abstract List<Pos> getPossibleMoves(Pos pos);

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

    public final ImageIcon getBoardBackground(Dimension fieldDim) {
        var background = new BoardBackgroundGenerator(fieldDim);
        return background.background;
    }

    public int getNumberOfPlayers() {
        return maxPlayers;
    }

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
    abstract public boolean move(Pos p0, Pos p1);

    public void endTurn() {
        tempMoveList.clear();
    }

    public void rollBack() {
        if (tempMoveList.isEmpty())
            return;
        var start = tempMoveList.get(0);
        var end = tempMoveList.get(tempMoveList.size() - 1);
        int swap = board.get(start.y).get(start.x);
        board.get(start.y).set(start.x, board.get(end.y).get(end.x));
        board.get(end.y).set(end.x, swap);
        tempMoveList = new ArrayList<>(); //clear tempMoveList
    }

    abstract public boolean canMove(Pos pos);

    abstract public List<Integer> getWinners();

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
                    drawBackgroundFieldBase(new Pos(x, y), g, fieldDim);
                }
            }

            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(4));

            //draws lines between fields
            for (int y = 0; y < defaultBoard.size(); y++) {
                for (int x = 0; x < defaultBoard.get(0).size(); x++) {
                    if (defaultBoard.get(y).get(x) == null)
                        continue;
                    var neighs = getNeighbors(new Pos(x, y));
                    for (Pos p : neighs) {
                        if (p != null)
                            g.drawLine((x + 1) * fieldDim.width + fieldDim.width / 2,
                                    (y + 1) * fieldDim.height + fieldDim.height / 2,
                                    (p.x + 1) * fieldDim.width + fieldDim.width / 2,
                                    (p.y + 1) * fieldDim.height + fieldDim.height / 2);
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
        private void drawBackgroundFieldBase(Pos pos, Graphics2D g, Dimension fieldDim) {
            if (defaultBoard.get(pos.y).get(pos.x) >= 0) { // field is a base
                List<Pos> neighs = getNeighbors(pos);
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
        private void drawBackgroundFieldBaseTile(Pos pos, Pos neigh0, Pos neigh1, Graphics g, Dimension fieldDim) {
            if (neigh0 == null || neigh1 == null)
                return;

            int[] xs = {(pos.x + 1) * fieldDim.width + fieldDim.width / 2,
                    (neigh0.x + 1) * fieldDim.width + fieldDim.width / 2,
                    (neigh1.x + 1) * fieldDim.width + fieldDim.width / 2};
            int[] ys = {(pos.y + 1) * fieldDim.height + fieldDim.height / 2,
                    (neigh0.y + 1) * fieldDim.height + fieldDim.height / 2,
                    (neigh1.y + 1) * fieldDim.height + fieldDim.height / 2};

            g.setColor(colorScheme.get(defaultBoard.get(pos.y).get(pos.x)));
            g.fillPolygon(xs, ys, 3);
        }
    }
}
