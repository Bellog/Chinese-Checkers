package org.example.server.gameModes;

import org.example.Pos;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StandardGameModeTest {

    StandardGameMode game2 = new StandardGameMode(2);
    StandardGameMode game6 = new StandardGameMode(6);

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