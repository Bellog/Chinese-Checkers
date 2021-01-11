package org.example.server.gameModes;

import org.example.Pos;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardGameModeTest {

    StandardGameMode game2 = new StandardGameMode(2);
    StandardGameMode game6 = new StandardGameMode(6);

    @Test
    void getStartingBoardForTwo() {
        int maxPlayers = 2, temp;
        StandardGameMode game = new StandardGameMode(maxPlayers);
        List<Integer> fieldCount = new ArrayList<>();
        for (int i = 0; i <= maxPlayers; i++) {
            fieldCount.add(0);
        }
        // counting numbers of fields
        for (int y = 0; y < game.getBoard().size(); y++) {
            for (int x = 0; x < game.getBoard().get(y).size(); x++) {
                if (game.getBoard().get(y).get(x) != null) {
                    temp = fieldCount.get(game.getBoard().get(y).get(x) + 1);
                    fieldCount.set(game.getBoard().get(y).get(x) + 1, temp + 1);
                }
            }
        }
        // check the number of empty fields
        assertEquals(fieldCount.get(0), 61 + (10 * (6 - maxPlayers)));
        // check if all players have 10 pawns
        for (int i = 1; i <= maxPlayers; i++) {
            assertEquals(fieldCount.get(i), 10);
        }
    }

    @Test
    void getStartingBoardForThree() {
        int maxPlayers = 3, temp;
        StandardGameMode game = new StandardGameMode(maxPlayers);
        List<Integer> fieldCount = new ArrayList<>();
        for (int i = 0; i <= maxPlayers; i++) {
            fieldCount.add(0);
        }
        // counting numbers of fields
        for (int y = 0; y < game.getBoard().size(); y++) {
            for (int x = 0; x < game.getBoard().get(y).size(); x++) {
                if (game.getBoard().get(y).get(x) != null) {
                    temp = fieldCount.get(game.getBoard().get(y).get(x) + 1);
                    fieldCount.set(game.getBoard().get(y).get(x) + 1, temp + 1);
                }
            }
        }
        // check the number of empty fields
        assertEquals(fieldCount.get(0), 61 + (10 * (6 - maxPlayers)));
        // check if all players have 10 pawns
        for (int i = 1; i <= maxPlayers; i++) {
            assertEquals(fieldCount.get(i), 10);
        }
    }

    @Test
    void getStartingBoardForFour() {
        int maxPlayers = 4, temp;
        StandardGameMode game = new StandardGameMode(maxPlayers);
        List<Integer> fieldCount = new ArrayList<>();
        for (int i = 0; i <= maxPlayers; i++) {
            fieldCount.add(0);
        }
        // counting numbers of fields
        for (int y = 0; y < game.getBoard().size(); y++) {
            for (int x = 0; x < game.getBoard().get(y).size(); x++) {
                if (game.getBoard().get(y).get(x) != null) {
                    temp = fieldCount.get(game.getBoard().get(y).get(x) + 1);
                    fieldCount.set(game.getBoard().get(y).get(x) + 1, temp + 1);
                }
            }
        }
        // check the number of empty fields
        assertEquals(fieldCount.get(0), 61 + (10 * (6 - maxPlayers)));
        // check if all players have 10 pawns
        for (int i = 1; i <= maxPlayers; i++) {
            assertEquals(fieldCount.get(i), 10);
        }
    }

    @Test
    void getStartingBoardForSix() {
        int maxPlayers = 6, temp;
        StandardGameMode game = new StandardGameMode(maxPlayers);
        List<Integer> fieldCount = new ArrayList<>();
        for (int i = 0; i <= maxPlayers; i++) {
            fieldCount.add(0);
        }
        // counting numbers of fields
        for (int y = 0; y < game.getBoard().size(); y++) {
            for (int x = 0; x < game.getBoard().get(y).size(); x++) {
                if (game.getBoard().get(y).get(x) != null) {
                    temp = fieldCount.get(game.getBoard().get(y).get(x) + 1);
                    fieldCount.set(game.getBoard().get(y).get(x) + 1, temp + 1);
                }
            }
        }
        // check the number of empty fields
        assertEquals(fieldCount.get(0), 61);
        // check if all players have 10 pawns
        for (int i = 1; i <= maxPlayers; i++) {
            assertEquals(fieldCount.get(i), 10);
        }
    }

    Pos getPosEmpty () {
        int i = 0, x = 0, y = -1;
        List<List<Integer>> board = game6.getBoard();
        while (i != -1) {
            y++;
            x = 0;
            while (i != -1 && x < board.get(y).size() - 1) {
                x++;
                if (board.get(y).get(x) != null)
                    i = board.get(y).get(x);
            }
        }
        return new Pos(x, y);
    }

    Pos getPosPlayer () {
        int i = -1, x = 0, y = -1;
        List<List<Integer>> board = game6.getBoard();
        while (i == -1) {
            y++;
            x = 0;
            while (i == -1 && x < board.get(y).size() - 1) {
                x++;
                if (board.get(y).get(x) != null)
                    i = board.get(y).get(x);
            }
        }
        return new Pos(x, y);
    }

    @Test
    void getPossibleMoves() {
        assertTrue(game6.getPossibleMoves(getPosPlayer()).isEmpty());
    }

    @Test
    void move() {
    }

    @Test
    void canMove() {
        //assertTrue(game6.canMove(new Pos(7, 12)));
    }
}