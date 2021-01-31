package org.example.server.gameModes;

import static org.junit.jupiter.api.Assertions.*;

import org.example.connection.Pos;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardGameModeTest {

    StandardGameMode gameEx;

    // some tests may alter the game, e.g. the board
    @BeforeEach
    public void beforeEachInit() {
        gameEx = new StandardGameMode(3);
    }

    // check if a 2-players game gets properly created
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

    // check if a 3-players game gets properly created
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

    // check if a 4-players game gets properly created
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

    // check if a 6-players game gets properly created
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

    @Test
    void getPossibleMovesCannotMoveToNeighbor() {
        Pos pos0 = new Pos(gameEx.getBoard().get(0).size()/2, 2);
        // pos0 jumps to pos1
        Pos pos1 = new Pos(gameEx.getBoard().get(0).size()/2 - 2, 4);
        // pos2 is a neighbor to pos1
        Pos pos2 = new Pos(gameEx.getBoard().get(0).size()/2, 4);
        gameEx.tempMoveList.add(pos0);
        gameEx.tempMoveList.add(pos1);
        gameEx.getBoard().get(pos1.y).set(pos1.x, 0);
        assertFalse(gameEx.getPossibleMoves(pos1).contains(pos2));
    }

    @Test
    void getPossibleMovesCannotMoveAfterNeighbor() {
        Pos pos0 = new Pos(gameEx.getBoard().get(0).size()/2 - 1, 3);
        // pos0 is a neighbor to pos1
        Pos pos1 = new Pos(gameEx.getBoard().get(0).size()/2, 4);
        gameEx.tempMoveList.add(pos0);
        gameEx.tempMoveList.add(pos1);
        gameEx.getBoard().get(pos1.y).set(pos1.x, 0);
        // player cannot move after neighbor-move
        assertNull(gameEx.getPossibleMoves(pos1));
    }

    @Test
    void getPossibleMovesCannotMoveOutsideWinCondition() {
        Pos pos0 = new Pos(11, 13);
        // pos0 is a neighbor to pos1
        Pos pos1 = new Pos(10, 12);
        gameEx.tempMoveList.add(pos0);
        gameEx.getBoard().get(pos0.y).set(pos0.x, 0);
        // let some field in win condition area be empty, for possible moves list not to be empty
        gameEx.getBoard().get(13).set(13, -1);
        // player cannot move outside the win condition area
        assertFalse(gameEx.getPossibleMoves(pos0).contains(pos1));
    }

    @Test
    void isWinnerSuccessful() {
        for (int y = 0; y < gameEx.getBoard().size(); y++)
            for (int x = 0; x < gameEx.getBoard().get(y).size(); x++)
                if (gameEx.getBoard().get(y).get(x) != null && gameEx.getDefaultBoard().get(y).get(x) == 3)
                    gameEx.getBoard().get(y).set(x, 0);
        assertTrue(gameEx.isWinner(0));
    }

    @Test
    void isWinnerUnsuccessful() {
        gameEx.getBoard().get(13).set(13, 0);
        assertFalse(gameEx.isWinner(0));
    }

    @Test
    void moveSuccessfulJump() {
        Pos start = new Pos(gameEx.getBoard().get(0).size()/2, 2);
        Pos finish = new Pos(gameEx.getBoard().get(0).size()/2 - 2, 4);
        // check jump move
        assertTrue(gameEx.move(start, finish));
        // move list should not be empty
        assertFalse(gameEx.tempMoveList.isEmpty());
        gameEx.tempMoveList.clear();
    }

    @Test
    void moveSuccessfulNeighbor() {
        Pos start = new Pos(gameEx.getBoard().get(0).size()/2 - 1, 3);
        Pos finish = new Pos(gameEx.getBoard().get(0).size()/2, 4);
        // check neighbor move
        assertTrue(gameEx.move(start, finish));
        // move list should not be empty
        assertFalse(gameEx.tempMoveList.isEmpty());
        gameEx.tempMoveList.clear();
    }

    @Test
    void moveUnsuccessful() {
        Pos start = new Pos(gameEx.getBoard().get(0).size()/2 - 1, 3);
        // finish field already taken
        Pos finish1 = new Pos(gameEx.getBoard().get(0).size()/2, 2);
        // finish field out of possible moves
        Pos finish2 = new Pos(gameEx.getBoard().get(0).size()/2 - 1, 5);
        assertFalse(gameEx.move(start, finish1));
        assertFalse(gameEx.move(start, finish2));
    }

    @Test
    void canMove() {
        int maxPlayers = gameEx.maxPlayers, temp;
        List<Integer> fieldCount = new ArrayList<>();
        for (int i = 0; i < maxPlayers; i++)
            fieldCount.add(0);
        // find all pawns that can be moved
        for (int y = 0; y < gameEx.getBoard().size(); y++) {
            for (int x = 0; x < gameEx.getBoard().get(y).size(); x++) {
                if (gameEx.getBoard().get(y).get(x) != null && gameEx.getBoard().get(y).get(x) != -1) {
                    if (gameEx.canMove(new Pos(x, y))) {
                        temp = fieldCount.get(gameEx.getBoard().get(y).get(x));
                        fieldCount.set(gameEx.getBoard().get(y).get(x), temp + 1);
                    }
                }
            }
        }
        // At the beginning each player should be able to move 7 out of 10 of their pawns
        for (int i = 0; i < maxPlayers; i++) {
            assertEquals(fieldCount.get(i), 7);
        }
    }
}