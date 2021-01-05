package org.example.server.gameModes;

import org.example.server.gameModes.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class AvailableGameModes {
    // commit change comment
    public enum GameModes {
        // For each new class in gameModes package, add new game mode id here
        BASIC,
        STANDARD
    }

    private final Map<GameModes, ArrayList<Integer>> maxPlayersMap;

    public AvailableGameModes () {
        maxPlayersMap = new TreeMap<>();
        // Should put the list from a class whenever one is created (see enum GameModes)
        maxPlayersMap.put(GameModes.BASIC, new ArrayList<>() {{add(2); add(3); add(4); add(6);}});
        maxPlayersMap.put(GameModes.STANDARD, new ArrayList<>() {{add(2); add(3); add(4); add(6);}});

    }

    public AbstractGameMode getGameMode (GameModes mode, int maxPlayers) {
        // number of players must be correct
        if (maxPlayersMap.get(mode).contains(maxPlayers)) {
            return switch (mode) {
                case BASIC -> new BasicGameMode(maxPlayers);
                case STANDARD -> new StandardGameMode(maxPlayers);
            };
        }
        return null;
    }
}
