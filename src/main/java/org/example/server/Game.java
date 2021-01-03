package org.example.server;

import org.example.ARuleSet;
import org.example.BasicRuleSet;
import org.example.Pair;

import java.awt.*;
import java.util.List;

public class Game implements IGame {

    //TODO make it so that you choose game mode in cli when server starts
    private final ARuleSet ruleSet;
    //TODO remove Color.WHITE and change all usages of this list
    //background color should be chosen by client, not the game itself
    private final List<Color> colorScheme = List.of(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW);

    /**
     * Number of players required to play a game.
     */
    private final int numberOfPlayers = 2;

    public Game() {
        ruleSet = new BasicRuleSet();
    }

    @Override
    public int getBoardHeight() {
        return ruleSet.getBoard().size();
    }

    @Override
    public int getBoardWidth() {
        return ruleSet.getBoard().get(0).size();
    }

    @Override
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    @Override
    public Pair getFieldInfo(int x, int y) {
        try {
            return ruleSet.getBoard().get(y).get(x);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public boolean move(int x0, int y0, int x1, int y1) {
        int state = ruleSet.getBoard().get(y1).get(x1).first;
        if (state < 0 || state == ruleSet.getBoard().get(y0).get(x0).second) {
            ruleSet.getBoard().get(y1).set(x1, new Pair(
                    ruleSet.getBoard().get(y0).get(x0).first,
                    ruleSet.getBoard().get(y1).get(x1).second));
            ruleSet.getBoard().get(y0).set(x0, new Pair(
                    state, ruleSet.getBoard().get(y0).get(x0).second));
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
