package org.example.server;

import org.example.connection.Packet;
import org.junit.jupiter.api.*;
import org.mockito.stubbing.Answer;

import java.awt.*;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

/**
 * Server class' tests have to be in a single method, as many methods are
 * dependent on each other, by using FixMethodOrder, this test can be split into
 * multiple methods so that it is clear which part is incorrect
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerTest {

    private final PrintStream out = System.out;

    private final int players = 3;
    private final Server server = new Server();
    private GameHandler handler;
    private ServerConnection conn;

    @BeforeAll
    public void setup() {
        System.setOut(mock(PrintStream.class)); //remove system.out text in test output
        handler = mock(GameHandler.class);
        conn = mock(ServerConnection.class);
        AtomicInteger i = new AtomicInteger();

        when(conn.addPlayer()).then((Answer<Boolean>) invocation -> {
            server.handlePacket(i.getAndIncrement(), new Packet.PacketBuilder().code(Packet.Codes.APP_INFO)
                    .fieldDim(mock(Dimension.class)).build());
            return true;
        });
        when(handler.getNumberOfPlayers()).thenReturn(players);
    }

    /**
     * Tests game initialization
     * <p></p>
     * This test may fail if server.init() takes too long to execute.
     */
    @Test
    @Order(1)
    public void testGameStart() {
        server.init(handler, conn);
        assertNotEquals(null, server.getVersion());

        try {
            Thread.sleep(300); // init works in background thread, this makes sure it completes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(conn, times(players)).addPlayer();
        verify(handler, times(1)).gameStart(notNull());
        // info that the game has started should be sent from handler which is mocked
        verify(conn, times(0)).sendToPlayer(anyInt(), notNull());
    }

    /**
     * Tests whether players can disconnect and rejoin
     */
    @Test
    @Order(2)
    public void testReconnect() {
        server.handlePacket(0, new Packet.PacketBuilder().code(Packet.Codes.CONNECTION_LOST).build());
        try {
            Thread.sleep(300); // addPlayer invoked by handlePacket works in background thread, this makes sure it completes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*
            When connection is lost, server should:
            - notify all players that someone has left
            - rollback that players turn
            - invoke addPlayer on serverConnection and wait till a player connect (in this test it happens instantly)
            - notify all other players that the player has reconnected + that all players have reconnected
            Therefore:
                conn should handle a packet: 3 * (players -1 ) <- other players
                gameHandler.joinPlayer should be invoked

            Above results may depend on testReconnectFail
         */
        verify(conn, times(3 * (players - 1))).sendToPlayer(anyInt(), notNull());
        verify(handler, times(1)).joinPlayer(anyInt(), notNull());
    }

    @Test
    @Order(3)
    public void testReconnectFail() {
        // conn needs to be reset, this method could be in its own test class, but it would introduce a lot of dupilacted code
        reset(conn);
        when(conn.addPlayer()).thenReturn(false);
        server.handlePacket(0, new Packet.PacketBuilder().code(Packet.Codes.CONNECTION_LOST).build());
        try {
            Thread.sleep(300); // addPlayer invoked by handlePacket works in background thread, this makes sure it completes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*
         Similar situation as in testReconnect, but addPlayer returns false, which means:
          - conn should handle a packet: 2 * (players -1 ), as game is not resumed
          - gameHandler.joinPlayer should not be invoked
          note that conn mock is reset, so there is no need to account for previous calls,
          on the other hand joinPlayer calls should remain the same - one
         */
        verify(conn, times(2 * (players - 1))).sendToPlayer(anyInt(), notNull());
        verify(handler, times(1)).joinPlayer(anyInt(), notNull());
    }

    @AfterAll
    public void after() {
        System.setOut(out);
    }
}