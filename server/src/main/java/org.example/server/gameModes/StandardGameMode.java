package org.example.server.gameModes;

import org.example.connection.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Game mode with polished methods that apply rules of Chinese Checkers.
 */
class StandardGameMode extends BasicGameMode {

    public StandardGameMode(int maxPlayers) {
        super(maxPlayers);
    }

    protected List<Pos> getPossibleMoves(Pos pos) {
        // if field state is -1 and is a neighbor then player can move there
        // if field state is -1 and is across a non-negative neighbor field, then player can move there
        List<Pos> list = getNeighbors(pos);
        List<Pos> temp = new ArrayList<>();
        for (Pos p : list)
            // check if there are any taken fields you can jump over.
            if (p != null && board.get(p.y).get(p.x) >= 0)
                temp.add(neighborCheck(p, p.x - pos.x, p.y - pos.y));
        list.addAll(temp);
        // check if this is not the first move
        if (tempMoveList.size() > 1) {
            // your cannot move if you moved to a neighbor field.
            if (getNeighbors(tempMoveList.get(0)).contains(tempMoveList.get(1)))
                return null;
                // you cannot move to neighbor fields if you jumped.
            else
                list.removeAll(getNeighbors(pos));
        }

        int player = board.get(pos.y).get(pos.x);
        if (winCondition.get(player).contains(pos)) //if player is in the base, they cannot move outside of it
            list.removeIf(p -> !winCondition.get(player).contains(p));

        return list.stream()
                // filter for empty fields
                .filter(p -> p != null && board.get(p.y).get(p.x) == -1)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean isWinner(int player) {
        for (int i = 0; i < winCondition.get(player).size(); i++) {
            if (board.get(winCondition.get(player).get(i).y).get(winCondition.get(player).get(i).x) != player)
                return false;
        }
        return true;
    }

    /**
     * Uses canMove method.
     * <br>Adds entries to the list of moves in a turn.
     *
     * @param start Moving pawn
     * @param finish Field you want to move to
     * @return True if a move was successful, false otherwise
     */
    public boolean move(Pos start, Pos finish) {
        // if temporary moveList is empty then, the turn has just started (or have been rolled back
        if (tempMoveList.isEmpty())
            tempMoveList.add(start);
        int temp = getBoard().get(finish.y).get(finish.x);
        if (getPossibleMoves(start).contains(finish)) {
            // swap given fields if you are allowed to move
            board.get(finish.y).set(finish.x, board.get(start.y).get(start.x));
            board.get(start.y).set(start.x, temp);
            tempMoveList.add(finish);
            return true;
        }
        return false;
    }

    public boolean canMove(Pos pos) {
        List<Pos> list = getPossibleMoves(pos);
        return list != null && !list.isEmpty();
    }
}
