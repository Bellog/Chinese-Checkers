package org.example.server;


import org.example.Pos;
import org.example.connection.Packet;
import org.example.server.gameModes.AbstractGameMode;
import org.example.server.gameModes.AvailableGameModes;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class GameHandlerTest {

    private final int players = 4;
    private final PrintStream systemOut = System.out;
    private int current = 0;
    private GameHandler handler;
    private IServer server;
    private AbstractGameMode mode;

    @BeforeAll
    public void classSetup() {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                //DO NOTHING
            }
        }));
    }

    @BeforeEach
    public void setup() throws IllegalAccessException {
        mode = AvailableGameModes.getGameMode(AvailableGameModes.GameModes.BASIC, players);
        assertNotNull(mode);
        mode = mock(mode.getClass()); // mocking requires concrete classes
        when(mode.getNumberOfPlayers()).thenReturn(players);
        when(mode.getWinners()).thenReturn(Collections.nCopies(players, null));

        server = mock(Server.class);
        handler = new GameHandler(mode, server);
        var f = ReflectionSupport.findFields(handler.getClass(), field -> field.getName().equals("currentPlayer"), HierarchyTraversalMode.BOTTOM_UP);
        assertEquals(1, f.size()); // there should by only one field with name currentPlayer
        f.get(0).setAccessible(true);
        current = f.get(0).getInt(handler);
    }

    @Test
    public void testConstructorNull() {
        // overwrites handler to test special case
        handler = new GameHandler(null, server);
        verify(server, times(1)).stop();
    }

    /**
     * This also tests handleInput partially
     */
    @Test
    public void testConstructor() {
        // each player but the current one should get information that they cannot perform any action
        // in other words: current player is set automatically during construction
        for (int i = 0; i < players; i++) {
            handler.handleInput(i, new Packet.PacketBuilder().code(Packet.Codes.INFO).build());
        }
        verify(server, times(players - 1)).sendToPlayer(anyInt(), notNull());
    }

    /**
     * This method also test joinPlayer (it cannot be tested individually as its behavior depends on current player
     * which is a private property)
     */
    @Test
    public void testGameStart() {
        handler.gameStart(Collections.nCopies(players, new Dimension(1, 1)));
        /*
          Send appropriate info to every player and notify all of them who's turn has started
        */
        verify(server, times(players * 2)).sendToPlayer(anyInt(), notNull());
    }

    @Test
    public void testMoveWrongPlayer() {
        when(mode.getFieldInfo(anyInt(), anyInt())).thenReturn(current + 1);

        // actual start and end do not matter
        handler.handleInput(current, new Packet.PacketBuilder().code(Packet.Codes.TURN_MOVE)
                .start(new Pos(1, 1)).end(new Pos(2, 2)).build());
        verify(server, times(1)).sendToPlayer(eq(current), notNull());
    }

    @Test
    public void testMoveSuccess() {
        // verification for these methods is inferred, mockito fails the test if mocked method is not used
        when(mode.getFieldInfo(anyInt(), anyInt())).thenReturn(current);
        when(mode.canMove(notNull())).thenReturn(true);
        when(mode.move(notNull(), notNull())).thenReturn(true);

        handler.handleInput(current, new Packet.PacketBuilder().code(Packet.Codes.TURN_MOVE)
                .start(new Pos(1, 1)).end(new Pos(2, 2)).build());
        // all other player should get information that someone has moved
        // + all players should get board update
        verify(server, times(players - 1 + players)).sendToPlayer(anyInt(), notNull());
    }

    @Test
    public void testMoveFailA() {
        // verification for these methods is inferred, mockito fails the test if mocked method is not used
        when(mode.getFieldInfo(anyInt(), anyInt())).thenReturn(current);
        when(mode.canMove(notNull())).thenReturn(false);

        handler.handleInput(current, new Packet.PacketBuilder().code(Packet.Codes.TURN_MOVE)
                .start(new Pos(1, 1)).end(new Pos(2, 2)).build());
        // only current player should get any info
        verify(server, times(1)).sendToPlayer(anyInt(), notNull());
    }

    @Test
    public void testMoveFailB() {
        // verification for these methods is inferred, mockito fails the test if mocked method is not used
        when(mode.getFieldInfo(anyInt(), anyInt())).thenReturn(current);
        when(mode.canMove(notNull())).thenReturn(true);
        when(mode.move(notNull(), notNull())).thenReturn(false);

        handler.handleInput(current, new Packet.PacketBuilder().code(Packet.Codes.TURN_MOVE)
                .start(new Pos(1, 1)).end(new Pos(2, 2)).build());
        // only current player should get any info
        verify(server, times(1)).sendToPlayer(eq(current), notNull());
        verify(server, times(0)).sendToPlayer(intThat(v -> v != current), notNull());
    }

    @Test
    public void testEndTurn() {
        handler.handleInput(current, new Packet.PacketBuilder().code(Packet.Codes.TURN_END).build());
        // turn_end code + other players' turn
        verify(server).sendToPlayer(eq(current), ArgumentMatchers
                .argThat(c -> c.getCode().equals(Packet.Codes.TURN_END)));
        // every one should get info that the turn has started
        verify(server, times(players - 1)).sendToPlayer(anyInt(),
                argThat(c -> c.getCode().equals(Packet.Codes.INFO)));
        verify(server).sendToPlayer(eq((current + 1) % players),
                argThat(c -> c.getCode().equals(Packet.Codes.TURN_START)));
    }

    @Test
    public void testRollback() {
        handler.handleInput(current, new Packet.PacketBuilder().code(Packet.Codes.TURN_ROLLBACK).build());
        verify(mode, times(1)).rollBack();
        verify(server, times(players)).sendToPlayer(anyInt(),
                argThat(c -> c.getCode().equals(Packet.Codes.BOARD_UPDATE)));
    }

    @Test
    public void testCheckWinners() {
        List<Integer> winners = new ArrayList<>();
        winners.add(0); //player 0 is a winner
        for (int i = 1; i < players; i++)
            winners.add(null);

        when(mode.getWinners()).thenReturn(winners);
        when(mode.getFieldInfo(anyInt(), anyInt())).thenReturn(current);
        when(mode.canMove(notNull())).thenReturn(true);
        when(mode.move(notNull(), notNull())).thenReturn(true);

        handler.handleInput(current, new Packet.PacketBuilder().code(Packet.Codes.TURN_MOVE)
                .start(new Pos(1, 1)).end(new Pos(2, 2)).build());

        verify(server, times(players)).sendToPlayer(anyInt(),
                argThat(c -> c.getCode().equals(Packet.Codes.PLAYER_UPDATE)));
    }

    @AfterEach
    public void afterEach() {
        verify(server, times(0)).sendToPlayer(anyInt(), isNull());
    }

    @AfterAll
    public void after() {
        System.setOut(systemOut);
    }
}
