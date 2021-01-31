package org.example.server.gameModes;

import org.example.connection.Pos;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AbstractGameModeTest {

    static AbstractGameMode game;

    @BeforeAll
    public static void beforeAllInit() {
        game = new BasicGameMode(3);
    }

    @Test
    void getBoardBackground() {
        assertNotNull(game.getBoardBackground(new Dimension(1, 1)));
    }

    @Test
    void endTurnClearingMoveList() {
        game.tempMoveList.add(new Pos(0, 0));
        game.endTurn();
        assertTrue(game.tempMoveList.isEmpty());
    }

    @Test
    void rollBack() {
        Pos start = new Pos(12, 4);
        Pos middle = new Pos(10, 10);
        Pos finish = new Pos(13, 13);
        int startValue = game.getBoard().get(start.y).get(start.x);
        int finishValue = game.getBoard().get(finish.y).get(finish.x);
        // add some items to move list
        game.tempMoveList.add(start);
        game.tempMoveList.add(middle);
        game.tempMoveList.add(finish);
        game.rollBack();
        // check if the move list was cleared
        assertTrue(game.tempMoveList.isEmpty());
        // check if values were swapped
        assertEquals(game.getBoard().get(start.y).get(start.x), finishValue);
        assertEquals(game.getBoard().get(finish.y).get(finish.x), startValue);
    }
}