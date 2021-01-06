package org.example.server.gameModes;

import org.example.Pos;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

class BasicGameMode extends AbstractGameMode {

    public BasicGameMode(int maxPlayers) {
        super(maxPlayers, List.of(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW));
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

    @Override
    protected List<List<Pos>> getWinCondition() {
        List<List<Pos>> list = new ArrayList<>();
        for (int player = 0; player < maxPlayers; player++) {
            list.add(new ArrayList<>());
            for (int y = 0; y < board.size(); y++) {
                for (int x = 0; x < board.get(y).size(); x++) {
                    if (board.get(y).get(x) != null && board.get(y).get(x) == player)
                        list.get(player).add(new Pos(board.get(0).size() - 1 - x, board.size() - 1 - y));
                    // board is symmetrical, so that if (x,y) is player's base, then (maxX -x, maxY - y)
                    // is one of the fields that player needs to go to
                }
            }
        }
        return list;
    }

    @Override
    protected List<Pos> getNeighbors(Pos pos) {
        List<Pos> list = new ArrayList<>();

        list.add(neighborCheck(pos, 1, -1));
        list.add(neighborCheck(pos, 2, 0));
        list.add(neighborCheck(pos, 1, 1));
        list.add(neighborCheck(pos, -1, 1));
        list.add(neighborCheck(pos, -2, 0));
        list.add(neighborCheck(pos, -1, -1));

        return list;
    }

    @Override
    protected List<Pos> getPossibleMoves(Pos pos) {
        // if field state is -1 and is a neighbor then player can move there
        return getNeighbors(pos).stream()
                .filter(p -> p != null && board.get(p.y).get(p.x) == -1)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getWinners() {
        int place = (int) winners.stream().filter(Objects::nonNull).count();
        for (int i = 0; i < maxPlayers; i++) {
            if (winners.get(i) == null && isWinner(i)) {
                winners.set(i, place);
                break;
            }
        }
        return winners;
    }

    protected boolean isWinner(int player) {
        for (int j = 0; j < winCondition.get(player).size(); j++) {
            if (board.get(winCondition.get(player).get(j).y).get(winCondition.get(player).get(j).x) == player)
                return true;
        }
        return false;
    }

    @Override
    protected List<List<Integer>> getStartingBoard() {
        List<List<Integer>> board = new ArrayList<>();
        for (int y = 0; y < defaultBoard.size(); y++) {
            board.add(new ArrayList<>());
            for (int x = 0; x < defaultBoard.get(0).size(); x++) {
                if (defaultBoard.get(y).get(x) != null && defaultBoard.get(y).get(x) > -2) {
                    if (defaultBoard.get(y).get(x) == -1)
                        board.get(y).add(-1);
                    else board.get(y).add(playerMap.get(maxPlayers).get(defaultBoard.get(y).get(x)));
                } else
                    board.get(y).add(null);
            }
        }
        return board;
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
    public Integer getFieldInfo(int x, int y) {
        try {
            return board.get(y).get(x);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public boolean move(Pos p0, Pos p1) {
        int state = getBoard().get(p1.y).get(p1.x);
        if (state == -1) {
            board.get(p1.y).set(p1.x, board.get(p0.y).get(p0.x));
            board.get(p0.y).set(p0.x, state);
            return true;
        }
        return false;
    }

    @Override
    public boolean canMove(Pos pos) {
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
