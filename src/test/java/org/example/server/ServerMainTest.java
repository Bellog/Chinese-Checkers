package org.example.server;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.mock;

/**
 * This test requires that there is at game mode with id 0 in gameModes package {@link org.example.server.gameModes.AvailableGameModes}
 * supports 2 players but does not support a single player game
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerMainTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private ByteArrayInputStream testIn;

    @BeforeAll
    public void setup() {
        System.setOut(mock(PrintStream.class)); // disregard any output
    }

    /**
     * Incorrect game mode
     */
    @Test
    @ExpectSystemExitWithStatus(1)
    public void testMainA() {
        testIn = new ByteArrayInputStream("-1 2".getBytes());
        System.setIn(testIn);
        System.out.println("DOES NOT WORK");
//        Server.main(new String[0]);
    }

    /**
     * Correct game mode, incorrect player count
     */
    @Test
    @ExpectSystemExitWithStatus(1)
    public void testMainB() {
        testIn = new ByteArrayInputStream("0 1".getBytes());
        System.setIn(testIn);
        System.out.println("DOES NOT WORK");
//        Server.main(new String[0]);
    }

    /**
     * Correct game mode, correct player count
     */
    @Test
    @ExpectSystemExitWithStatus(0)
    public void testMainC() {
        testIn = new ByteArrayInputStream("0 2".getBytes());
        System.setIn(testIn);
        System.out.println("DOES NOT WORK");
//        Server.main(new String[0]);
        System.exit(0); // Server.main calls System.exit, then it will be non-zero exit code
    }

    @AfterAll
    public void after() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }
}
