package org.example.server.gameModes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AvailableGameModesTest {

    @Test
    void getPlayerNumberListEntriesNotNull() {
        // check if all modes in enum have entries
        for (AvailableGameModes.GameModes mode : AvailableGameModes.GameModes.values())
            assertNotNull(AvailableGameModes.getPlayerNumberList(mode));
    }

    @Test
    void getGameModeGettingGames() {
        Random random = new Random();
        int index = random.nextInt(4);
        int maxPlayersBasic = AvailableGameModes.getPlayerNumberList(AvailableGameModes.GameModes.BASIC).get(index);
        int maxPlayersStandard = AvailableGameModes.getPlayerNumberList(AvailableGameModes.GameModes.STANDARD).get(index);
        // check if a randomly given game option can be created
        assertNotNull(AvailableGameModes.getGameMode(AvailableGameModes.GameModes.BASIC, maxPlayersBasic));
        assertNotNull(AvailableGameModes.getGameMode(AvailableGameModes.GameModes.STANDARD, maxPlayersStandard));
        // illegal numbers of players should result in null
        assertNull(AvailableGameModes.getGameMode(AvailableGameModes.GameModes.BASIC, 5));
        assertNull(AvailableGameModes.getGameMode(AvailableGameModes.GameModes.STANDARD, 5));
    }
}