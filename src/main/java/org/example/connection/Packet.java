package org.example.connection;

import org.example.Pair;

import javax.swing.*;
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
    private final List<List<Integer>> board;
    private final Integer playerId;
    private final String message;
    private final Pair startPos;
    private final Pair endPos;
    private final List<Color> colors;
    private final ImageIcon image;

    private Packet(PacketBuilder builder) {
        code = builder.code;
        board = builder.board;
        playerId = builder.playerId;
        message = builder.message;
        startPos = builder.start;
        endPos = builder.end;
        colors = builder.colorScheme;
        image = builder.image;
    }

    public Codes getCode() {
        return code;
    }

    public List<List<Integer>> getBoard() {
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

    public ImageIcon getImage() {
        return image;
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
        TURN_END,
        TURN_ROLLBACK,
        GAME_START,     //board, playerId, colorScheme
        GAME_END,
        GAME_PAUSE,
        GAME_RESUME;
        //Add more actions as needed, then change version inside pom.xml to ensure integrity between client and server

        /**
         * Used by Serializable interface, do not change
         */
        private static final long serialVersionUID = 1002L;
    }

    public static class PacketBuilder {
        private Codes code = null;
        private List<List<Integer>> board = null;
        private String message = null;
        private Integer playerId = null;
        private Pair start = null;
        private Pair end = null;
        private List<Color> colorScheme = null;
        private ImageIcon image;

        public PacketBuilder code(Codes code) {
            this.code = code;
            return this;
        }

        public PacketBuilder playerId(int playerId) {
            this.playerId = playerId;
            return this;
        }

        public PacketBuilder board(List<List<Integer>> board) {
            this.board = board;
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

        public PacketBuilder image(ImageIcon image) {
            this.image = image;
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
