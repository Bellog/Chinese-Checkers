package org.example.connection;

import org.example.ARuleSet;
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
    private final Integer playerId;
    private final String message;
    private final Pair startPos;
    private final Pair endPos;
    private final List<Color> colors;
    private final ARuleSet ruleSet;

    private Packet(PacketBuilder builder) {
        code = builder.code;
        board = builder.board;
        playerId = builder.value;
        message = builder.message;
        startPos = builder.start;
        endPos = builder.end;
        colors = builder.colorScheme;
        ruleSet = builder.ruleSet;
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

    public Integer getPlayerId() {
        return playerId;
    }

    public Pair getStartPos() {
        return startPos;
    }

    public Pair getEndPos() {
        return endPos;
    }

    public List<Color> getColorScheme() {
        return colors;
    }

    public ARuleSet getRuleSet() {
        return ruleSet;
    }

    /**
     * List of possible code Packet class can be used to send
     */
    public enum Codes implements Serializable {
        INFO,
        BOARD_UPDATE,
        PLAYER_TURN,
        PLAYER_MOVE,
        ACTION_SUCCESS,
        WRONG_ACTION,
        ACTION_FAILURE,
        OPPONENT_TURN,
        OPPONENT_MOVE,
        GAME_START,     //board, playerId, colorScheme
        GAME_END;
        //Add more actions as needed, then change version inside pom.xml to ensure integrity between client and server

        /**
         * Used by Serializable interface, do not change
         */
        private static final long serialVersionUID = 1002L;
    }

    public static class PacketBuilder {
        private Codes code = null;
        private List<List<Pair>> board = null;
        private String message = null;
        private Integer value = null;
        private Pair start = null;
        private Pair end = null;
        private List<Color> colorScheme = null;
        private ARuleSet ruleSet = null;

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

        public PacketBuilder ruleSet(ARuleSet ruleSet) {
            this.ruleSet = ruleSet;
            return this;
        }

        public PacketBuilder colorScheme(List<Color> colorScheme) {
            this.colorScheme = colorScheme;
            return this;
        }

        public Packet build() {
            return new Packet(this);
        }
    }
}
