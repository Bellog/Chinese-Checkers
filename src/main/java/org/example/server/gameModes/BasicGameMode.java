package org.example.server.gameModes;

import org.example.Pair;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

class BasicGameMode extends AbstractGameMode {

    public BasicGameMode(int maxPlayers) {
        super(maxPlayers, List.of(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW));

        for (int y = 0; y < defaultBoard.size(); y++) {
            board.add(new ArrayList<>());
            for (int x = 0; x < defaultBoard.get(0).size(); x++) {
                if (defaultBoard.get(y).get(x) != null && defaultBoard.get(y).get(x) > -2) {
                    if (defaultBoard.get(y).get(x) == -1)
                        board.get(y).add(-1);
                    else board.get(y).add(playerBases.get(maxPlayers).get(defaultBoard.get(y).get(x)));
                } else
                    board.get(y).add(null);
            }
        }
    }

    @Override
    protected Map<Integer, Map<Integer, Integer>> getPlayerMap() {
        Map<Integer, Map<Integer, Integer>> map = new TreeMap<>();
        map.put(2, new TreeMap<>() {{
            put(0, 0);
            put(1, -1);
            put(2, -1);
            put(3, 1);
            put(4, -1);
            put(5, -1);
        }});
        map.put(3, new TreeMap<>() {{
            put(0, 0);
            put(1, 1);
            put(2, -1);
            put(3, -1);
            put(4, -1);
            put(5, 2);
        }});
        map.put(4, new TreeMap<>() {{
            put(0, -1);
            put(1, 0);
            put(2, 1);
            put(3, -1);
            put(4, 2);
            put(5, 3);
        }});
        map.put(6, new TreeMap<>() {{
            put(0, 0);
            put(1, 1);
            put(2, 2);
            put(3, 3);
            put(4, 4);
            put(5, 5);
        }});
        return map;
    }

    protected List<List<Pair>> getWinCondition() {
        List<List<Pair>> list = new ArrayList<>();
        for (int i = 0; i < maxPlayers; i++) {
            list.add(new ArrayList<>());
            for (int j = 0; j < defaultBoard.size(); j++) {
                for (int k = 0; k < defaultBoard.get(j).size(); k++) {
                    if (defaultBoard.get(j).get(k) == (i + 3) % 6)
                        list.get(i).add(new Pair(k, j));
                }
            }
        }
        return list;
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
    protected List<Pair> getPossibleMoves(Pair pos) {
        // if field state is -1 and is a neighbor then player can move there
        return getNeighbors(pos).stream()
                .filter(p -> p != null && board.get(p.second).get(p.first) == -1)
                .collect(Collectors.toList());
    }

    @Override
    public int winnerId() {
        for (int i = 0; i < maxPlayers; i++) {
            if (isWinner(i))
                return i;
        }
        return -1;
    }

    protected boolean isWinner(int i) {
        for (int j = 0; j < winCondition.get(i).size(); j++) {
            if (board.get(winCondition.get(i).get(j).second).get(winCondition.get(i).get(j).first) != i)
                return false;
        }
        return true;
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
    public boolean move(Pair p0, Pair p1) {
        int state = getBoard().get(p1.second).get(p1.first);
        if (state == -1) {
            board.get(p1.second).set(p1.first, board.get(p0.second).get(p0.first));
            board.get(p0.second).set(p0.first, state);
            return true;
        }
        return false;
    }

    @Override
    public boolean canMove(Pair pos) {
        return true;
    }

    @Override
    public List<Color> getColorScheme() {
        var map = getPlayerMap().get(getNumberOfPlayers());
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            colors.add(null);
        }
        // this loop adds only those colors that have their bases in the game (i.e. only for players 1 and 3)
        // by removing nulls from colors afterwards, the result is effectively applying this map onto colorscheme
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() != -1) {
                colors.add(entry.getValue(), colorScheme.get(entry.getKey()));
            }
        }

        return colors.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
