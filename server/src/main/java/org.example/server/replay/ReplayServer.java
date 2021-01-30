package org.example.server.replay;

import com.google.common.collect.HashBiMap;
import org.example.connection.Packet;
import org.example.server.IGameHandler;
import org.example.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Profile("replay")
public class ReplayServer extends Server {

    private final ApplicationContext context;
    private volatile Queue<Move> moveQueue;
    private volatile int currentPlayer;

    @Autowired
    public ReplayServer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void setGameHandler(IGameHandler handler) {
        gameHandler = handler;
        playerMap = HashBiMap.create();
        playerMap.put(0, "null0");
        if (moveQueue != null) // initialization requires both handler and replay
            initialized = true;
    }

    public void setReplay(GameSave replay) {
        currentPlayer = replay.getMoves().get(0).getPlayer();
        this.moveQueue = new LinkedBlockingQueue<>(replay.getMoves());
        if (gameHandler != null) // initialization requires both handler and replay
            initialized = true;
    }

    @Override
    public synchronized void handlePacket(String playerId, Packet packet) {
        if (!initialized) return;
        switch (packet.getCode()) {
            case DISCONNECT, CONNECT -> super.handlePacket(playerId, packet);
            default -> {

            }
        }
    }

    @Scheduled(fixedRate = 1500)
    @Bean("replay-exec")
    private void action() {
        if (!gameRunning)
            return;
        Move move = moveQueue.poll();
        if (move == null) {
            sendToPlayer(0, new Packet.PacketBuilder()
                    .code(Packet.Codes.GAME_END).playerId(currentPlayer).message("Replay has ended").build());
            ScheduledAnnotationBeanPostProcessor bean = context.getBean(ScheduledAnnotationBeanPostProcessor.class);
            bean.postProcessBeforeDestruction(this, "replay-exec");
            stop();
            return;
        }

        if (currentPlayer != move.getPlayer()) {
            gameHandler.handleInput(currentPlayer, new Packet.PacketBuilder()
                    .code(Packet.Codes.TURN_END).playerId(currentPlayer).build());
            currentPlayer = move.getPlayer();
        }

        gameHandler.handleInput(currentPlayer, new Packet.PacketBuilder()
                .code(Packet.Codes.TURN_MOVE).playerId(currentPlayer)
                .startPos(move.getStart()).endPos(move.getEnd()).build());
    }

    @Override
    public void sendToPlayer(int player, Packet packet) {
        if (player == 0)
            super.sendToPlayer(player, packet);
    }

    @Override
    public void stop() {
        System.out.println("Stopping the server");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
    }
}
