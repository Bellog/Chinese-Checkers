package org.example.server;

import org.example.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game implements IGame {

    private final List<List<Field>> board = new ArrayList<>();
    private final List<Color> colors = List.of(Color.BLACK, Color.RED, Color.GREEN, Color.BLUE);
    private final int boardHeight = 4;
    private final int boardWidth = 4;
    /**
     * Number of players required to play a game.
     */
    private final int numberOfPlayers = 3;

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

    @Override
    public int getBoardHeight() {
        return boardHeight;
    }

    @Override
    public int getBoardWidth() {
        return boardWidth;
    }

    @Override
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    @Override
    public Pair getFieldInfo(int x, int y) {
        try {
            return new Pair(board.get(y).get(x).getState(), board.get(y).get(x).getType());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

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

    @Override
    public List<Color> getColors() {
        return colors;
    }

    /**
     * In game field.
     * Holds information about its type and current state, refer to these field for more information.
     */
    public static class Field {

        /**
         * -1 represents normal field, values >= 0 represent base of player with that index in gameHandler
         */
        private final int type;

        /**
         * -1 represents unoccupied field, values >= 0 represent field occupied by player with that index in gameHandler
         */
        private volatile int state = -1;

        public Field(int type) {
            this.type = type;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getType() {
            return type;
        }

    }
}
