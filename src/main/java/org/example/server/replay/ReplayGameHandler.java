package org.example.server.replay;

import org.example.connection.Packet;
import org.example.server.IGameHandler;
import org.example.server.IServer;
import org.example.server.gameModes.AbstractGameMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Game handler used to show game replays. <br>
 * Does not validate any moves, as they should be recorded correctly. <br>
 * Assumes that there is only one connected player (client that shows the replay).
 */
public class ReplayGameHandler implements IGameHandler {

    private final AbstractGameMode game;
    private final IServer server;
    private List<Integer> winners;

    /**
     * @param mode   game mode
     * @param server not null
     */
    public ReplayGameHandler(AbstractGameMode mode, IServer server) {
        this.server = server;
        this.game = mode;
        if (game == null) {
            System.out.println("Parameters are null");
            server.stop();
            return;
        }
        winners = new ArrayList<>(game.getWinners()); // copies the list
    }

    @Override
    public void handleInput(int player, Packet packet) {
        switch (packet.getCode()) {
            case TURN_MOVE -> {
                if (game.move(packet.getStartPos(), packet.getEndPos())) {
                    server.sendToPlayer(-1, new Packet.PacketBuilder().code(Packet.Codes.BOARD_UPDATE)
                            .board(game.getBoard()).message("Player " + (player + 1) + " moved").build());
                    checkWinners();
                }
            }
            case TURN_END -> game.endTurn();
        }
    }

    private void checkWinners() {
        List<Integer> newWinners = new ArrayList<>(game.getWinners());
        // there is a new winner
        if (newWinners.stream().filter(Objects::nonNull).count() > winners.stream().filter(Objects::nonNull).count()) {
            // if the game should end (i.e. 2 players, 1 have won, newWinners may have places for both players,
            // get playerId of latest winner
            int player = 0;
            for (int i = 0; i < newWinners.size(); i++) {
                if (newWinners.get(player) < newWinners.get(i)) {
                    player = i;
                }
            }

            winners = newWinners;
            server.sendToPlayer(-1, new Packet.PacketBuilder().code(Packet.Codes.PLAYER_UPDATE)
                    .playerInfo(generatePlayerInfo())
                    .message("Player " + (player + 1) + " has won " + newWinners.get(player) + ". place!")
                    .build());
            if (newWinners.get(player) == game.getNumberOfPlayers() - 1) {
                server.sendToPlayer(-1, new Packet.PacketBuilder()
                        .code(Packet.Codes.GAME_END).message("The game has ended!").build());
                server.stop();
            }
        }
    }

    @Override
    public int getNumberOfPlayers() {
        return game.getNumberOfPlayers();
    }

    @Override
    public void gameStart(List<Dimension> fieldDims) {
        server.sendToPlayer(-1, new Packet.PacketBuilder()
                .code(Packet.Codes.GAME_START).colors(game.getColorScheme())
                .board(game.getBoard())
                .playerId(0)
                .image(game.getBoardBackground(fieldDims.get(0)))
                .playerInfo(generatePlayerInfo())
                .message("You have connected to game replay, you will not be able to perform any actions")
                .build());

        server.sendToPlayer(-1, new Packet.PacketBuilder()
                .code(Packet.Codes.TURN_END).build()); // end turn disables actions on client
    }

    @Override
    public void joinPlayer(int player, Dimension fieldDim) {
        // ignored
    }

    private List<List<String>> generatePlayerInfo() {
        List<List<String>> list = new ArrayList<>();

        for (int i = 0; i < game.getNumberOfPlayers(); i++) {
            list.add(new ArrayList<>());

            list.get(i).add("Player " + (i + 1));

            if (winners.get(i) == null) //position
                list.get(i).add("-");
            else
                list.get(i).add("" + (winners.get(i) + 1));
        }

        list.add(0, List.of("Player", "Pos"));
        return list;
    }

    /**
     * This class is used for replays only, it does not record the game
     *
     * @return null
     */
    @Override
    public GameSave getSave() {
        return null;
    }
}
