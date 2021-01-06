package org.example.server.gameModes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Factory-like object that can show list of game modes and instantiate them.
 */
public class AvailableGameModes {
    /**
     * PlayersMap is a collection that contains {@link GameModes} and a list of max players configurations
     */
    private static final Map<GameModes, List<Integer>> playersMap;

    static {
        playersMap = new TreeMap<>();
        // Should put the list for a class whenever one is created (see enum GameModes)
        playersMap.put(GameModes.BASIC, new ArrayList<>() {{
            add(2);
            add(3);
            add(4);
            add(6);
        }});
        playersMap.put(GameModes.STANDARD, new ArrayList<>() {{
            add(2);
            add(3);
            add(4);
            add(6);
        }});
    }

    /**
     * This class should not be instantiated.
     */
    private AvailableGameModes() {

    }

    /**
     * Returns list of available player configurations for supplied game mode
     *
     * @param mode game mode to be checked
     * @return list of player configurations
     */
    public static List<Integer> getPlayerNumberList(GameModes mode) {
        return playersMap.get(mode);
    }

    /**
     * @param mode       game mode to be created
     * @param maxPlayers game mode will be created for this many players
     * @return correct game mode, null if game mode does not exist of maxPlayers is incorrect
     */
    public static AbstractGameMode getGameMode(GameModes mode, int maxPlayers) {
        // number of players must be correct
        if (playersMap.get(mode).contains(maxPlayers)) {
            return switch (mode) {
                case BASIC -> new BasicGameMode(maxPlayers);
                case STANDARD -> new StandardGameMode(maxPlayers);
            };
        } else
            return null;
    }

    // commit change comment
    public enum GameModes {
        // For each new class in gameModes package, add new game mode id here
        BASIC,
        STANDARD
    }
}
