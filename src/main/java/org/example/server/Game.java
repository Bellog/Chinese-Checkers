package org.example.server;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final List<List<Field>> board = new ArrayList<>();
    private final List<String> marks = List.of("x", "o", "#");
    private final int boardHeight = 4;
    private final int boardWidth = 4;
    private final int numberOfPlayers = 3;

    public Game() {
        for (var i = 0; i < boardWidth; i++) {
            board.add(new ArrayList<>());
            if (i < numberOfPlayers) {
                board.get(0).add(new Field(i));
                board.get(0).get(i).setState(i);
            }
            else board.get(0).add(new Field(-1));
        }
        for (var i = 1; i < boardHeight; i++) {
            for (var j = 0; j < boardWidth; j++) {
                board.get(i).add(new Field(-1));
            }
        }
    }

    public int getBoardHeight() { return boardHeight; }

    public int getBoardWidth() { return boardWidth; }

    public int getNumberOfPlayers() { return numberOfPlayers; }

    public String getMark(int x, int y) { return board.get(y).get(x).getMark(); }

    public void setMarks() {
        for (var i = 0; i < boardHeight; i++) {
            for (var j = 0; j < boardWidth; j++) {
                int state = board.get(i).get(j).getState();
                if (state >= 0)
                    board.get(i).get(j).setMark(marks.get(state));
                else board.get(i).get(j).setMark(" ");
            }
        }
    }

    public boolean isMoveLegal(int x0, int y0, int x1, int y1) {
        int state = board.get(y1).get(x1).getState();
        if (state < 0 || state == board.get(y0).get(x0).getState()) {
            board.get(y1).get(x1).setState(board.get(y0).get(x0).getState());
            board.get(y0).get(x0).setState(state);
            return true;
        }
        return false;
    }

    public boolean hasWinner() {
        int check = 0;
        for (var i = 0; i < boardHeight; i++) {
            for (var j = 0; j < boardWidth; j++) {
                if (board.get(i).get(j).getBase() >= 0 && board.get(i).get(j).getState() < 0)
                    check++;
            }
        }
        return check >= numberOfPlayers;
    }

    /*
    this method is useless for chinese checkers, can be deleted later.
    */
    public boolean isFilledUp() {
        //return board.stream().noneMatch(v -> v.getMark().equals(" "));
        for (var i = 0; i < boardHeight; i++) {
            for (var j = 0; j < boardWidth; j++) {
                if (board.get(i).get(j).getState() < 0)
                    return false;
            }
        }
        return true;
    }

}
