package org.example.server.gameModes;

import org.example.server.gameModes.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class AvailableGameModes {
    public enum GameModes {
        // For each new class in gameModes package, add new game mode id here
        BASIC,
        STANDARD
    }

    private final Map<GameModes, ArrayList<Integer>> playerNumbersMap;

    public AvailableGameModes () {
        playerNumbersMap = new TreeMap<>();
        // Should put the list from a class whenever one is created (see enum GameModes)
        playerNumbersMap.put(GameModes.BASIC, BasicGameMode.possiblePlayerNumbers);
        playerNumbersMap.put(GameModes.STANDARD, StandardGameMode.possiblePlayerNumbers);

    }

    public AbstractGameMode getGameMode (GameModes mode, int maxPlayers) {
        return switch (mode) {
            case BASIC -> new BasicGameMode(maxPlayers);
            case STANDARD -> new StandardGameMode(maxPlayers);
            default -> null;
        };
    }
}
