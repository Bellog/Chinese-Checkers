package org.example.server.web;

import org.example.connection.Packet;
import org.example.server.IServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Objects;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate template;

    private IServer server;

    @Autowired
    public WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/game")
    public void handlePacket(@Payload Packet packet, Principal principal) {
        server.handlePacket(principal.getName(), packet);
    }

    public synchronized void sendToPlayer(String playerId, Packet packet) {
        template.convertAndSendToUser(playerId, "/queue/game", packet);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        server.handlePacket(Objects.requireNonNull(event.getUser()).getName(), new Packet.PacketBuilder()
                .code(Packet.Codes.DISCONNECT).build());
    }

    @Autowired
    public void setServer(IServer server) {
        this.server = server;
    }
}
