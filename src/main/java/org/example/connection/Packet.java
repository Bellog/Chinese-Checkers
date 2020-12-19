package org.example.connection;

import java.io.Serial;
import java.io.Serializable;

/**
 * Packet class used to send data between server and clients, each code from Codes enum determines which fields are set
 */
public class Packet implements Serializable {

    /**
     * Used by Serializable interface, do not change
     */
    private static final long serialVersionUID = 1001L;

    private final Codes code;
    private final String text;
    private final Integer value;
    private final String message;
    private final Position start;
    private final Position end;

    private Packet(PacketBuilder builder) {
        code = builder.code;
        text = builder.text;
        value = builder.value;
        message = builder.message;
        start = builder.start;
        end = builder.end;
    }

    public Codes getCode() {
        return code;
    }

    public String getBoard() {
        return text;
    }

    public String getMessage() {
        return message;
    }

    public Integer getValue() {
        return value;
    }

    public Position getStart() { return start; }

    public Position getEnd() { return end; }

    /**
     * List of possible code Packet class can be used to send
     */
    public enum Codes implements Serializable {
        INFO,
        PLAYER_INFO,
        OPPONENT_TURN,
        PLAYER_TURN,
        WRONG_ACTION,
        ACTION_SUCCESS,
        ACTION_FAILURE,
        GAME_END,
        BOARD_UPDATE,
        PLAYER_MOVE,
        OPPONENT_MOVE;
        //Add more actions as needed, then change version inside pom.xml to ensure integrity between client and server

        /**
         * Used by Serializable interface, do not change
         */
        private static final long serialVersionUID = 1002L;
    }

    public static class Position implements Serializable {
        private static final long serialVersionUID = 1003L;

        public final int x;
        public final int y;

        public Position (int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /*
        Alternatywnie może być kreator na podstawie kodów - np.: dla info jedynym parametrem jest text, dla innego więcej
     */
    public static class PacketBuilder {
        private Codes code = null;
        private String text = null;
        private String message = null;
        private Integer value = null;
        private Position start = null;
        private Position end = null;

        public PacketBuilder code(Codes code) {
            this.code = code;
            return this;
        }

        public PacketBuilder value(int value) {
            this.value = value;
            return this;
        }

        public PacketBuilder board(String text) {
            this.text = text;
            return this;
        }

        public PacketBuilder message(String message) {
            this.message = message;
            return this;
        }

        public PacketBuilder start(Position start) {
            this.start = start;
            return this;
        }

        public PacketBuilder end(Position end) {
            this.end = end;
            return this;
        }

        public Packet build() {
            return new Packet(this);
        }
    }
}
