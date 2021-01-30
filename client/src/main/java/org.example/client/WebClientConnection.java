package org.example.client;

import org.example.connection.Packet;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.awt.*;
import java.lang.reflect.Type;

@Service
@EnableWebSocket
public class WebClientConnection implements IClientConnection {

    private final WebSocketStompClient stompClient;
    private final Handler handler;
    private final int port = 8080;
    private IClient client;

    public WebClientConnection() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxBinaryMessageBufferSize(1024 * 1024 * 16);
        container.setDefaultMaxTextMessageBufferSize(1024 * 1024 * 16);
        WebSocketClient transport = new StandardWebSocketClient(container);
        stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setInboundMessageSizeLimit(1024 * 1024 * 16);
        handler = new Handler();
    }

    @Override
    public void init(Dimension fieldDim, IClient client) {
        this.client = client;
        stompClient.connect("ws://localhost:{port}/sternhalma", new WebSocketHttpHeaders(), handler, port);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.send(new Packet.PacketBuilder().code(Packet.Codes.CONNECT)
                .fieldDim(fieldDim).build());
    }

    @Override
    public void send(Packet packet) {
        handler.send(packet);
    }

    private class Handler extends StompSessionHandlerAdapter {

        private StompSession session;

        @Override
        @NonNull
        public Type getPayloadType(@NonNull StompHeaders headers) {
            return Packet.class;
        }

        @Override
        public void handleFrame(@NonNull StompHeaders headers, Object payload) {
            if ((payload instanceof Packet)) {
                client.handlePacket((Packet) payload);
            } else
                System.out.println("frame error");
        }

        @Override
        public void afterConnected(StompSession session, @NonNull StompHeaders connectedHeaders) {
            this.session = session;
            session.subscribe("/user/queue/game", this);
            client.handlePacket(new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("Found a game!").build());
        }

        @Override
        public void handleException(@NonNull StompSession session, StompCommand command,
                                    @NonNull StompHeaders headers, @NonNull byte[] payload, Throwable exception) {
            client.handlePacket(new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("Connection error: " + exception.getMessage()).build());
        }

        @Override
        public void handleTransportError(@NonNull StompSession session, Throwable exception) {
            client.handlePacket(new Packet.PacketBuilder().code(Packet.Codes.INFO)
                    .message("transport error: " + exception.getMessage()).build());
        }

        public void send(Packet packet) {
            session.send("/app/game", packet);
        }
    }
}
