package org.example.server.gameModes;

import org.example.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BasicGameMode extends AbstractGameMode {

    //TODO make it so that you choose game mode in cli when server starts
    private final List<Color> colorScheme = List.of(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW);

    // for compatibility sake
    public BasicGameMode() {
        this(2);
    }

    public BasicGameMode(int maxPlayers) {
        super(maxPlayers);
        // maxPlayers can be only 2,3,4 or 6
        if (maxPlayers != 2 && maxPlayers != 3 && maxPlayers != 4 && maxPlayers != 6)
            return;

        for (int y = 0; y < 17; y++) {
            board.add(new ArrayList<>());
            for (int x = 0; x < 25; x++) {
                if (defaultBoard.get(y).get(x) != null && defaultBoard.get(y).get(x) > -2) {
                    if (isCorrectBase(new Pair(x, y)))
                        board.get(y).add(defaultBoard.get(y).get(x));
                    else
                        board.get(y).add(-1);
                } else
                    board.get(y).add(null);
            }
        }
    }

    /**
     * Helper method that determines whether particular field from baseBoard is a base
     * according to number of players
     * <p></p> Offload entire logic with respect to number of players
     *
     * @param pos position to check
     * @return whether field is a base according to number of players
     */
    private boolean isCorrectBase(Pair pos) {
        return switch (maxPlayers) {
            case 2 -> List.of(0, 3).contains(defaultBoard.get(pos.second).get(pos.first));
            case 3 -> List.of(0, 1, 5).contains(defaultBoard.get(pos.second).get(pos.first));
            case 4 -> List.of(1, 2, 4, 5).contains(defaultBoard.get(pos.second).get(pos.first));
            case 6 -> true;
            default -> false;
        };
    }

    @Override
    protected List<Pair> getNeighbors(Pair pos) {
        List<Pair> list = new ArrayList<>();

        list.add(neighborCheck(pos, 1, -1));
        list.add(neighborCheck(pos, 2, 0));
        list.add(neighborCheck(pos, 1, 1));
        list.add(neighborCheck(pos, -1, 1));
        list.add(neighborCheck(pos, -2, 0));
        list.add(neighborCheck(pos, -1, -1));

        return list;
    }

    @Override
    public List<Pair> getPossibleMoves(Pair pos) {
        // if field state is -1 and is a neighbor then player can move there
        return getNeighbors(pos).stream()
                .filter(p -> p != null && board.get(p.second).get(p.first) == -1)
                .collect(Collectors.toList());
    }

    @Override
    public int winnerId() {
        return -1;
    }

    /**
     * int array where >= 0 means base of player with this id, -1 means nobody's base, -2 means no field <p></p>
     *
     * @return field type array
     */
    @Override
    protected List<List<Integer>> getDefaultBoard() {
        int[][] bases = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {7, 0, 7, 0, 7, 0, 7, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 3, 0, 3, 0, 3, 0, 3},
                {0, 7, 0, 7, 0, 7, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 3, 0, 3, 0, 3, 0},
                {0, 0, 7, 0, 7, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 3, 0, 3, 0, 0},
                {0, 0, 0, 7, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 3, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 6, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 4, 0, 0, 0},
                {0, 0, 6, 0, 6, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 4, 0, 4, 0, 0},
                {0, 6, 0, 6, 0, 6, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 4, 0, 4, 0, 4, 0},
                {6, 0, 6, 0, 6, 0, 6, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 4, 0, 4, 0, 4, 0, 4},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 5, 0, 5, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 5, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };
        List<List<Integer>> list = new ArrayList<>();

        for (int y = 0; y < bases.length; y++) {
            list.add(new ArrayList<>());
            for (int x = 0; x < bases[0].length; x++) {
                //subtracts 2 from every field, so that every number in bases is 1 digit long
                if (bases[y][x] > 0)
                    list.get(y).add(bases[y][x] - 2);
                else
                    list.get(y).add(null);
            }
        }

        return list;
    }

    @Override
    public int getNumberOfPlayers() {
        return super.maxPlayers;
    }

    @Override
    public Integer getFieldInfo(int x, int y) {
        try {
            return board.get(y).get(x);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public boolean move(int x0, int y0, int x1, int y1) {
        int state = getBoard().get(y1).get(x1);
        if (state < 0 || state == defaultBoard.get(y0).get(x0)) {
            board.get(y1).set(x1, board.get(y0).get(x0));
            board.get(y0).set(x0, state);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasWinner() {
        return false;
    }

    @Override
    public List<Color> getColorScheme() {
        return colorScheme;
    }

}