package org.example.server;

import org.example.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Includes logic of a specific type of a game and operates the board.
 */
public class Game implements IGame {

    /**
     * Game board overview.
     */
    private final List<List<Field>> board = new ArrayList<>();
    //    private final List<String> marks = List.of("x", "o", "#");
    /**
     * List of player colors.
     */
    private final List<Color> colors = List.of(Color.BLACK, Color.RED, Color.GREEN, Color.BLUE);
    /**
     * Vertical dimension of the board.
     */
    private final int boardHeight = 4;
    /**
     * Horizontal dimension of the board.
     */
    private final int boardWidth = 4;
    /**
     * Number of players required to play a game.
     */
    private final int numberOfPlayers = 3;

    /**
     * Class constructor.
     */
    public Game() {
        for (var i = 0; i < boardWidth; i++) {
            board.add(new ArrayList<>());
            if (i < numberOfPlayers) {
                board.get(0).add(new Field(i));
                board.get(0).get(i).setState(i);
            } else board.get(0).add(new Field(-1));
        }
        for (var i = 1; i < boardHeight; i++) {
            for (var j = 0; j < boardWidth; j++) {
                board.get(i).add(new Field(-1));
            }
        }
    }

    /**
     * Height getter.
     * @return vertical dimension of the board.
     */
    @Override
    public int getBoardHeight() {
        return boardHeight;
    }

    /**
     * Width getter.
     * @return horizontal dimension of the board.
     */
    @Override
    public int getBoardWidth() {
        return boardWidth;
    }

    /**
     * Number getter.
     * @return required number of players.
     */
    @Override
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Used to access information about occupation and significance of fields on board.
     * @param x coordinate
     * @param y coordinate
     * @return state and base of a field, null if no such coordinates were found.
     */
    @Override
    public Pair getFieldInfo(int x, int y) {
        try {
            return new Pair(board.get(y).get(x).getState(), board.get(y).get(x).getBase());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Rules should be defined in a separate class and be used here to determine outcome of the move
     *
     * @return true if move is successful, false if not
     */
    @Override
    public boolean move(int x0, int y0, int x1, int y1) {
        int state = board.get(y1).get(x1).getState();
        if (state < 0 || state == board.get(y0).get(x0).getState()) {
            board.get(y1).get(x1).setState(board.get(y0).get(x0).getState());
            board.get(y0).get(x0).setState(state);
            return true;
        }
        return false;
    }

    /**
     * Win condition.
     * @return true if a situation on the board is game-ending, false otherwise.
     */
    @Override
    public boolean hasWinner() {
//        int check = 0;
//        for (var i = 0; i < boardHeight; i++) {
//            for (var j = 0; j < boardWidth; j++) {
//                if (board.get(i).get(j).getBase() >= 0 && board.get(i).get(j).getState() < 0)
//                    check++;
//            }
//        }
        return board.get(boardWidth - 1).get(boardHeight - 1).getState() >= 0;

//        return check >= numberOfPlayers;
    }

    /**
     * Colors getter.
     * @return list of colors.
     */
    @Override
    public List<Color> getColors() {
        return colors;
    }

    /**
     * Represents pawns and empty spots on the board.
     */
    public static class Field {

        /**
         * -1 represents unoccupied field, values >= 0 represent field occupied by player with that index in gameHandler
         */
        private final int base;
        /**
         * -1 represents normal field, values >= 0 represent base of player with that index in gameHandler
         */
        private volatile int state = -1;

        /**
         * Class constructor.
         * @param base empty spot or starting point of a player.
         */
        public Field(int base) {
            this.base = base;
        }

        /**
         * State getter.
         * @return number of a player if he occupies the field, -1 otherwise.
         */
        public int getState() {
            return state;
        }

        /**
         * State setter
         * @param state occupation of a field.
         */
        public void setState(int state) {
            this.state = state;
        }

        /**
         * Base getter
         * @return number of a player if he started the game on this field, -1 otherwise.
         */
        public int getBase() {
            return base;
        }

    }
}
