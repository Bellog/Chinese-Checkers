package org.example.server.gameModes;

import org.example.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class StandardGameMode extends BasicGameMode {

    public StandardGameMode(int maxPlayers) {
        super(maxPlayers);
    }

    protected List<Pair> getPossibleMoves(Pair pos) {
        // if field state is -1 and is a neighbor then player can move there
        // if field state is -1 and is across a non-negative neighbor field, then player can move there
        List<Pair> list = getNeighbors(pos);
        List<Pair> temp = new ArrayList<>();
        for (Pair p : list)
            // check if there are any taken fields you can jump over.
            if (p != null && board.get(p.second).get(p.first) >= 0)
                temp.add(neighborCheck(p, p.first - pos.first, p.second - pos.second));
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
        return list.stream()
                // filter for empty fields
                .filter(p -> p != null && board.get(p.second).get(p.first) == -1)
                .collect(Collectors.toList());
    }

    public boolean move(Pair start, Pair finish) {
        // temporary move list cannot be empty
        if (tempMoveList.isEmpty())
            tempMoveList.add(start);
        int temp = getBoard().get(finish.second).get(finish.first);
        if (getPossibleMoves(start).contains(finish)) {
            // swap given fields if you are allowed to move
            board.get(finish.second).set(finish.first, board.get(start.second).get(start.first));
            board.get(start.second).set(start.first, temp);
            tempMoveList.add(finish);
            return true;
        }
        //rollBack();
        return false;
    }

    public boolean canMove (Pair pos) {
        List<Pair> list = getPossibleMoves(pos);
        return list != null && !list.isEmpty();
    }
}
