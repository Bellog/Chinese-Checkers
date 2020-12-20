package org.example.connection;

import org.example.Pair;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * Packet class used to send data between server and clients, each code from Codes enum determines which fields are set
 */
public class Packet implements Serializable {

    /**
     * Used by Serializable interface, do not change
     */
    private static final long serialVersionUID = 1001L;

    private final Codes code;
    private final List<List<Pair>> board;
    private final Integer value;
    private final String message;
    private final Pair startPos;
    private final Pair endPos;
    private final List<Color> colors;

    private Packet(PacketBuilder builder) {
        code = builder.code;
        board = builder.board;
        value = builder.value;
        message = builder.message;
        startPos = builder.start;
        endPos = builder.end;
        colors = builder.colors;
    }

    public Codes getCode() {
        return code;
    }

    public List<List<Pair>> getBoard() {
        return board;
    }

    public String getMessage() {
        return message;
    }

    public Integer getValue() {
        return value;
    }

    public Pair getStartPos() {
        return startPos;
    }

    public Pair getEndPos() {
        return endPos;
    }

    public List<Color> getColors() {
        return colors;
    }

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
        OPPONENT_MOVE,
        PLAYER_COLORS;
        //Add more actions as needed, then change version inside pom.xml to ensure integrity between client and server

        /**
         * Used by Serializable interface, do not change
         */
        private static final long serialVersionUID = 1002L;
    }

    /*
        Alternatywnie może być kreator na podstawie kodów - np.: dla info jedynym parametrem jest text, dla innego więcej
     */
    public static class PacketBuilder {
        private Codes code = null;
        private List<List<Pair>> board = null;
        private String message = null;
        private Integer value = null;
        private Pair start = null;
        private Pair end = null;
        private List<Color> colors = null;

        public PacketBuilder code(Codes code) {
            this.code = code;
            return this;
        }

        public PacketBuilder value(int value) {
            this.value = value;
            return this;
        }

        public PacketBuilder board(List<List<Pair>> board) {
            this.board = List.copyOf(board);
            return this;
        }

        public PacketBuilder message(String message) {
            this.message = message;
            return this;
        }

        public PacketBuilder start(Pair start) {
            this.start = start;
            return this;
        }

        public PacketBuilder end(Pair end) {
            this.end = end;
            return this;
        }

        public PacketBuilder colors(List<Color> colors) {
            this.colors = colors;
            return this;
        }

        public Packet build() {
            return new Packet(this);
        }
    }
}
