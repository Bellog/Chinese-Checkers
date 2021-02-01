package org.example.server.replay;

import org.example.connection.Pos;
import org.example.server.gameModes.AvailableGameModes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSaveTest {

    private GameSave save;

    @BeforeEach
    public void setup() {
        save = new GameSave();
    }

    @Test
    void testMode() {
        save.setGameMode(AvailableGameModes.GameModes.BASIC);
        assertEquals(AvailableGameModes.GameModes.BASIC, save.getMode());
    }

    @Test
    void testTurn() {
        save.setGameMode(AvailableGameModes.GameModes.BASIC);
        save.setPlayers(2);
        save.addMove(new Pos(0, 1), new Pos(2, 3), 0);
        save.addMove(new Pos(4, 5), new Pos(6, 7), 0);
        save.commitTurn();
        // check if all moves have been saved
        assertEquals(2, save.getMoves().size());
        save.addMove(new Pos(8, 9), new Pos(10, 11), 1);
        save.rollbackTurn();
        // check if rollback was correctly done
        assertNotEquals(3, save.getMoves().size());
        assertEquals(2, save.getMoves().size());
    }

    @Test
    void testGetMovesNotNull() {
        assertNotNull(save.getMoves());
    }

    @Test
    void testGetMovesAddNull() {
        save.setGameMode(AvailableGameModes.GameModes.BASIC);
        save.setPlayers(2);
        Move move = new Move();
        save.addMove(move.getStart(), move.getEnd(), move.getPlayer());
        assertNull(save.getMoves().get(0).getStart());
        assertNull(save.getMoves().get(0).getEnd());
        assertEquals(-1, save.getMoves().get(0).getPlayer());
    }

    @Test
    void testGetMovesCorrect() {
        save.setGameMode(AvailableGameModes.GameModes.BASIC);
        save.setPlayers(2);
        save.addMove(new Pos(0, 1), new Pos(2, 3), 1);
        assertEquals(0, save.getMoves().get(0).getStart().x);
        assertEquals(1, save.getMoves().get(0).getStart().y);
        assertEquals(2, save.getMoves().get(0).getEnd().x);
        assertEquals(3, save.getMoves().get(0).getEnd().y);
        // get player
        assertEquals(1, save.getMoves().get(0).getPlayer());
    }

    @Test
    void testGetPlayers() {
        save.setPlayers(3);
        assertEquals(3, save.getPlayers());
    }

    @Test
    void testToString() {
        save.setGameMode(AvailableGameModes.GameModes.BASIC);
        save.setPlayers(2);
        assertEquals("game BASIC for 2 players and with 0 moves", save.toString());
    }
}