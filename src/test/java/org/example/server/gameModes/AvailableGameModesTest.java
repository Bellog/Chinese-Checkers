package org.example.server.gameModes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvailableGameModesTest {

    @Test
    void getPlayerNumberList() {
        assertNotNull(AvailableGameModes.getPlayerNumberList(AvailableGameModes.GameModes.BASIC));
        assertNotNull(AvailableGameModes.getPlayerNumberList(AvailableGameModes.GameModes.STANDARD));
        for (int i = 0; i < AvailableGameModes.getPlayerNumberList(AvailableGameModes.GameModes.BASIC).size(); i++) {
            assertEquals(AvailableGameModes.getPlayerNumberList(AvailableGameModes.GameModes.BASIC).get(i),
                    AvailableGameModes.getPlayerNumberList(AvailableGameModes.GameModes.STANDARD).get(i));
        }
    }

    @Test
    void getGameMode() {
        AbstractGameMode game = AvailableGameModes.getGameMode(AvailableGameModes.GameModes.BASIC, 2);
        assertNotNull(AvailableGameModes.getGameMode(AvailableGameModes.GameModes.BASIC, 2));
        assertNull(AvailableGameModes.getGameMode(AvailableGameModes.GameModes.BASIC, 1));
        assertNull(AvailableGameModes.getGameMode(AvailableGameModes.GameModes.STANDARD, 5));
    }
}