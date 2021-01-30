package org.example.server.replay;

import org.example.connection.Packet;
import org.example.server.IServer;
import org.example.server.gameModes.AvailableGameModes;

import java.util.List;

public class Replay {

    private final IServer server;

    private final List<Move> moves;

    public Replay(GameSave save) {
        var mode = AvailableGameModes.getGameMode(save.getMode(), save.getPlayers());
        moves = save.getMoves();
        var server = new ReplayServer();
        server.init(new ReplayGameHandler(mode, server), new ReplayServerConnection(server));

        this.server = server;
    }

    public void initGame() {
        int currentPlayer = moves.get(0).getPlayer();
        for (Move move : moves) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (currentPlayer != move.getPlayer()) {
//                server.handlePacket(currentPlayer, new Packet.PacketBuilder().code(Packet.Codes.TURN_END).build());
                currentPlayer = move.getPlayer();
            }

//            server.handlePacket(currentPlayer, new Packet.PacketBuilder()
//                    .code(Packet.Codes.TURN_MOVE).startPos(move.getStart()).endPos(move.getEnd()).build());
        }
        server.sendToPlayer(-1, new Packet.PacketBuilder()
                .code(Packet.Codes.GAME_END).message("Replay has ended").build());
        server.stop();
    }
}
