package org.example.server;

import org.example.connection.Packet;
import org.example.connection.Pos;
import org.example.server.gameModes.AbstractGameMode;
import org.example.server.replay.GameSave;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handles a single game, ensures correct turn order and players' actions
 */
public class GameHandler implements IGameHandler {

    private final GameSave save;
    private final AbstractGameMode game;
    private final ReentrantLock LOCK = new ReentrantLock();
    private final IServer server;
    private List<Integer> winners;
    private int currentPlayer;

    /**
     * @param mode   game mode
     * @param server not null
     * @param save   game save
     */
    public GameHandler(AbstractGameMode mode, IServer server, GameSave save) {
        this.server = server;
        this.game = mode;
        this.save = save;
        if (game == null) {
            System.out.println("Parameters are null");
            server.stop();
            return;
        }
        winners = new ArrayList<>(game.getWinners()); // copies the list
        var rand = new Random();
        currentPlayer = rand.nextInt(game.getNumberOfPlayers());
    }

    @Override
    public void handleInput(int player, Packet packet) {
        LOCK.lock();
        if (player != currentPlayer) {
            server.sendToPlayer(player, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("It's not your turn!").build());
            LOCK.unlock();
            return;
        } else if (winners.get(player) != null) {
            LOCK.unlock();
            return; // the player is a winner and should not be able to do anything
        }

        switch (packet.getCode()) {
            case TURN_MOVE -> {
                if (move(packet.getStartPos(), packet.getEndPos())) {
                    save.addMove(packet.getStartPos(), packet.getEndPos(), currentPlayer);
                    for (int i = 0; i < game.getNumberOfPlayers(); i++)
                        server.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.BOARD_UPDATE)
                                .board(game.getBoard()).build());
                    checkWinners();
                }
            }
            case TURN_END -> endTurn();
            case TURN_ROLLBACK -> {
                game.rollBack();
                save.rollbackTurn();
                for (int i = 0; i < game.getNumberOfPlayers(); i++)
                    server.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.BOARD_UPDATE)
                            .board(game.getBoard()).build());
            }
        }
        LOCK.unlock();
    }

    @Override
    public int getNumberOfPlayers() {
        return game.getNumberOfPlayers();
    }

    @Override
    public void gameStart() {
        startTurn();
    }

    @Override
    public void joinPlayer(int player, Dimension fieldDim) {
        server.sendToPlayer(player, new Packet.PacketBuilder()
                .code(Packet.Codes.GAME_SETUP).colors(game.getColorScheme())
                .board(game.getBoard())
                .playerId(player)
                .image(game.getBoardBackground(fieldDim))
                .playerInfo(generatePlayerInfo(player))
                .message("You have joined the game as player " + (player + 1))
                .build());
    }

    /**
     * See {@link Packet.Codes#PLAYER_UPDATE} for more information
     *
     * @param playerId generate player info for this player
     * @return player info List
     */
    private List<List<String>> generatePlayerInfo(int playerId) {
        List<List<String>> list = new ArrayList<>();

        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            list.add(new ArrayList<>());

            if (i == playerId)
                list.get(i).add("Player " + (i + 1) + " (You)");
            else
                list.get(i).add("Player " + (i + 1));

            if (winners.get(i) == null) //position
                list.get(i).add("-");
            else
                list.get(i).add("" + (winners.get(i) + 1));
        }

        list.add(0, List.of("Player", "Pos"));
        return list;
    }

    private void startTurn() {
        server.sendToPlayer(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.TURN_START)
                .message("It's your turn now!").build());
        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            if (i != currentPlayer)
                server.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                        .message("Player " + (currentPlayer + 1) + " turn").build());
        }
    }

    private void endTurn() {
        server.sendToPlayer(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.TURN_END).build());
        do {
            currentPlayer = (currentPlayer + 1) % game.getNumberOfPlayers();
            System.out.println("current player:" + currentPlayer);
        } while (winners.get(currentPlayer) != null);
        //if winners[i] != null, then that player is a winner, they should not be able to play any more turns
        game.endTurn();
        save.commitTurn();
        startTurn();
    }

    /**
     * Checks whether a new player has won n-th place, if all but 1 player have won the game, ends it.
     */
    private void checkWinners() {
        List<Integer> newWinners = new ArrayList<>(game.getWinners());
        // there is a new winner
        if (newWinners.stream().filter(Objects::nonNull).count() > winners.stream().filter(Objects::nonNull).count()) {
            // if the game should end (i.e. 2 players, 1 have won, newWinners may have places for both players,
            //  in that case, using count() on newWinners could give incorrect results
            int place = (int) winners.stream().filter(Objects::nonNull).count() + 1;
            winners = newWinners;
            for (int i = 0; i < game.getNumberOfPlayers(); i++) {
                System.out.println("WINNERS: Player " + (currentPlayer + 1) + " has won " + place + ". place");
                server.sendToPlayer(i, new Packet.PacketBuilder().code(Packet.Codes.PLAYER_UPDATE)
                        .playerInfo(generatePlayerInfo(i))
                        .message("Player " + (currentPlayer + 1) + " has won " + place + ". place!")
                        .build());
                if (place == game.getNumberOfPlayers() - 1) {
                    server.sendToPlayer(i, new Packet.PacketBuilder()
                            .code(Packet.Codes.GAME_END).message("The game has ended!").build());
                    save.commitTurn();
                    server.stop();
                }
            }
            endTurn();
        }
    }

    /**
     * Moves player's pawn from start to end
     *
     * @param start start position
     * @param end   end position
     * @return true if move was successful, false means no changes were made
     */
    private boolean move(Pos start, Pos end) {
        if (game.getFieldInfo(start.x, start.y) != currentPlayer) {
            server.sendToPlayer(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("You cannot move somebody else's pawn!").build());
            return false;
        }
        if (game.canMove(start)) {
            if (game.move(start, end)) {
                for (int i = 0; i < game.getNumberOfPlayers(); i++)
                    if (i != currentPlayer)
                        server.sendToPlayer(i, new Packet.PacketBuilder()
                                .code(Packet.Codes.BOARD_UPDATE).board(game.getBoard())
                                .message("Player " + (currentPlayer + 1) + " moved").build());
                return true;
            } else {
                server.sendToPlayer(currentPlayer, new Packet.PacketBuilder()
                        .code(Packet.Codes.INFO).message("This field is already set").build());
                return false;
            }
        } else {
            server.sendToPlayer(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("You cannot move from this field").build());
            return false;
        }
    }

    @Override
    public GameSave getSave() {
        return save;
    }
}
