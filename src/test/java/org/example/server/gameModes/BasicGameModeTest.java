package org.example.server.gameModes;

import org.example.Pos;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class BasicGameModeTest {

    BasicGameMode game2 = new BasicGameMode(2);
    BasicGameMode game6 = new BasicGameMode(6);

    @Test
    void getWinners() {
        int place = (int) game6.getWinners().stream().filter(Objects::nonNull).count();
        assertEquals(place, 0);
    }

    @Test
    void getFieldInfo() {
        assertNull(game2.getFieldInfo(-1, -1));
        assertNull(game2.getFieldInfo(0, 0));
        assertEquals(game2.getFieldInfo(0, 4), -1);
        assertEquals(game6.getFieldInfo(0, 4), 5);
    }

    @Test
    void move() {
        int i = 0, x = 0, y = -1;
        Pos pos00 = new Pos(0, 0);
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
        Pos posTrue = new Pos(x, y);
        y = -1;
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
        assertTrue(game6.move(pos00, posTrue));
        assertFalse(game6.move(pos00, posFalse));
    }

    @Test
    void canMove() {
        assertTrue(game2.canMove(new Pos(0, 0)));
    }

    @Test
    void getColorScheme() {
        //
    }
}