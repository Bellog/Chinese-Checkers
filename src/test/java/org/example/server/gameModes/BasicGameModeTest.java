package org.example.server.gameModes;

import org.example.Pos;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class BasicGameModeTest {

    static BasicGameMode gameExample;

    @BeforeAll
    public static void beforeAllInit() {
        gameExample = new BasicGameMode(3);
    }

    @Test
    void getStartingBoardForTwo() {
        int maxPlayers = 2, temp;
        BasicGameMode game = new BasicGameMode(maxPlayers);
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
        BasicGameMode game = new BasicGameMode(maxPlayers);
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
        BasicGameMode game = new BasicGameMode(maxPlayers);
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
        BasicGameMode game = new BasicGameMode(maxPlayers);
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

    @Test
    void getWinners() {
        int place = (int) gameExample.getWinners().stream().filter(Objects::nonNull).count();
        assertEquals(place, 0);
    }

    @Test
    void getFieldInfo() {
        // no info about fields out of board
        assertNull(gameExample.getFieldInfo(-1, -1));
        // Player 2 has the point (4, 0)
        assertNotNull(gameExample.getFieldInfo(0, 4));
        assertNotEquals(gameExample.getFieldInfo(0, 4), -1);
        assertEquals(gameExample.getFieldInfo(0, 4), 2);
    }

    @Test
    void moveSuccessful() {
        int i = 0, x = 0, y = -1;
        // in Basic game mode you can move from everywhere
        Pos pos00 = new Pos(0, 0);
        // looking for empty field
        List<List<Integer>> board = gameExample.getBoard();
        while (i != -1) {
            y++;
            x = 0;
            while (i != -1 && x < board.get(y).size() - 1) {
                x++;
                if (board.get(y).get(x) != null)
                    i = board.get(y).get(x);
            }
        }
        Pos posTrue = new Pos(x, y);
        // you can move to an empty field
        assertTrue(gameExample.move(pos00, posTrue));
    }

    @Test
    void moveUnsuccessful() {
        int i = -1, x = 0, y = -1;
        // in Basic game mode you can move from everywhere
        Pos pos00 = new Pos(0, 0);
        List<List<Integer>> board = gameExample.getBoard();
        // looking for non-empty field
        while (i == -1) {
            y++;
            x = 0;
            while (i == -1 && x < board.get(y).size() - 1) {
                x++;
                if (board.get(y).get(x) != null)
                    i = board.get(y).get(x);
            }
        }
        Pos posFalse = new Pos (x, y);
        // you cannot move to an already taken field
        assertFalse(gameExample.move(pos00, posFalse));
    }

    @Test
    void canMove() {
        // you can move from any field
        assertTrue(gameExample.canMove(new Pos(0, 0)));
    }

    @Test
    void getColorSchemeNumber() {
        for (int n : AvailableGameModes.getPlayerNumberList(AvailableGameModes.GameModes.BASIC)) {
            BasicGameMode game = new BasicGameMode(n);
            // check if a game has a right amount of colors
            assertEquals(game.getColorScheme().size(), n);
        }
    }
}