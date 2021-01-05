package org.example.server;

import org.example.server.gameModes.*;

import java.util.Map;
import java.util.TreeMap;

public class AvailableGameModes {
    public enum GameModes {
        // For each new class in gameModes package, add new game mode id here
        BASIC,
        STANDARD
    }

    private final Map<GameModes, AbstractGameMode> gameModesMap;

    public AvailableGameModes () {
        gameModesMap = new TreeMap<>();
        gameModesMap.put(GameModes.BASIC, new BasicGameMode());
        gameModesMap.put(GameModes.STANDARD, new StandardGameMode());
    }

    public AbstractGameMode getGameMode (GameModes mode) { return gameModesMap.get(mode); }
}
