package org.example.server.gameModes;

import org.example.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StandardGameMode extends BasicGameMode {

    public List<Pair> getPossibleMoves(Pair pos) {
        // if field state is -1 and is a neighbor then player can move there
        // if field state is -1 and is across a non-negative neighbor field, then player can move there
        List<Pair> list = getNeighbors(pos);
        List<Pair> temp = new ArrayList<>();
        for (Pair p : list)
            if (p != null && board.get(p.second).get(p.first) >= 0)
                temp.add(neighborCheck(p, p.first - pos.first, p.second - pos.second));
        list.addAll(temp);
        return list.stream()
                .filter(p -> p != null && board.get(p.second).get(p.first) == -1)
                .collect(Collectors.toList());
    }

    public boolean move(Pair p0, Pair p1) {
        if (tempMoveList.isEmpty())
            tempMoveList.add(p0);
        int temp = getBoard().get(p1.second).get(p1.first);
        if (getPossibleMoves(p0).contains(p1)) {
            board.get(p1.second).set(p1.first, board.get(p0.second).get(p0.first));
            board.get(p0.second).set(p0.first, temp);
            tempMoveList.add(p1);
            return true;
        }
        //rollBack();
        return false;
    }
}
