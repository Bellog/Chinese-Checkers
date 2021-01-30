package org.example.connection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * Packet class used to send data between server and clients, each code from Codes enum determines which fields are set.
 * When writing method that handles packets should be wary of special codes, see {@link Packet.Codes} for more information.
 */
@JsonDeserialize(builder = Packet.PacketBuilder.class)
public class Packet implements Serializable {

    /**
     * Used by Serializable interface, do not change
     */
    private static final long serialVersionUID = 1001L;

    private final Codes code;
    private final List<List<Integer>> board;
    private final List<List<String>> playerInfo;
    private final Integer playerId;
    private final Dimension fieldDim;
    private final String message;
    private final Pos startPos;
    private final Pos endPos;
    private final List<Color> colors;
    private final ImageIcon image;

    /**
     * Private constructor ensures that this class can be instantiated only by using {@link Packet.PacketBuilder}.
     *
     * @param builder builder
     */
    private Packet(PacketBuilder builder) {
        code = builder.code;
        board = builder.board;
        playerInfo = builder.playerInfo;
        playerId = builder.playerId;
        message = builder.message;
        startPos = builder.startPos;
        endPos = builder.endPos;
        colors = builder.colors;
        image = builder.image;
        fieldDim = builder.fieldDim;
    }

    public Codes getCode() {
        return code;
    }

    /**
     *
     *
     * @return game board
     */
    public List<List<Integer>> getBoard() {
        return board;
    }

    /**
     * player information where each row is in format: Player [i] [(You)] | [position].
     * <br>First row is e header: players | pos
     *
     * @return player information
     */
    public List<List<String>> getPlayerInfo() {
        return playerInfo;
    }

    public String getMessage() {
        return message;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public Pos getStartPos() {
        return startPos;
    }

    public Pos getEndPos() {
        return endPos;
    }

    /**
     *
     *
     * @return color scheme list
     */
    public List<Color> getColors() {
        return colors;
    }

    /**
     *
     *
     * @return board background image
     */
    @JsonSerialize(using = ImageIconSerializer.class)
    public ImageIcon getImage() {
        return image;
    }

    /**
     * Single field dimension, see {@link Codes#CONNECT} for more information
     *
     * @return field dimension
     */
    public Dimension getFieldDim() {
        return fieldDim;
    }

    /**
     * List of possible code Packet class can be used to send.
     * Each code can also have a message.
     * If a code has a field required it must not be null.
     */
    public enum Codes implements Serializable {
        /**
         * Packet with no special functionality.
         */
        INFO,
        /**
         * Used by client to send information about fieldDim
         */
        CONNECT,
        /**
         * Updates playerInfo
         * <p></p> Requires fields:
         * <ul>
         *     <li>message</li>
         * </ul>
         */
        PLAYER_UPDATE,
        /**
         * updates board
         * <p></p> Requires fields:
         * <ul>
         *     <li>board</li>
         * </ul>
         */
        BOARD_UPDATE,
        /**
         * Server sends this to the the client that their turn starts
         */
        TURN_START,
        /**
         * Client sends this to move a pawn from startPos to endPos
         * <p></p> Requires fields:
         * <ul>
         *     <li>startPos</li>
         *     <li>startPos</li>
         * </ul>
         */
        TURN_MOVE,
        /**
         * Client sends this if it wants to end their turn
         */
        TURN_END,
        /**
         * Client sends this if it wants to reset their turn
         */
        TURN_ROLLBACK,
        /**
         * Server sends this to tell client to start the game
         * <p></p> Requires fields:
         * <ul>
         *     <li>board</li>
         *     <li>colorScheme</li>
         *     <li>playerId</li>
         *     <li>image</li>
         * </ul>
         */
        GAME_SETUP,
        /**
         * Server sends this to tell client that the game has ended
         */
        GAME_END,
        /**
         * Server sends this to tell client that the game has been paused
         */
        GAME_PAUSE,
        /**
         * Server sends this to tell client that the game has been resumed
         */
        GAME_RESUME,
        /**
         * Special packet, used internally to notify that caller has lost connection, i.e.
         * IServeConnection can send this packet to IServer when it loses server connection
         * <p></p> Requires fields:
         * <ul>
         *     <li>message</li>
         * </ul>
         */
        DISCONNECT;
        //Add more actions as needed, then change version inside pom.xml to ensure integrity between client and server

        /**
         * Used by Serializable interface, do not change
         */
        private static final long serialVersionUID = 1002L;
    }

    /**
     * Builder class for Packet, refer to {@link Packet.Codes} to see which fields you need to set.
     * Fields not set will be null.
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class PacketBuilder {
        private Codes code = null;
        private List<List<Integer>> board = null;
        private List<List<String>> playerInfo = null;
        private String message = null;
        private Integer playerId = null;
        private Dimension fieldDim = null;
        private Pos startPos = null;
        private Pos endPos = null;
        private List<Color> colors = null;
        private ImageIcon image;

        public PacketBuilder code(Codes code) {
            this.code = code;
            return this;
        }

        public PacketBuilder playerId(int playerId) {
            this.playerId = playerId;
            return this;
        }

        public PacketBuilder fieldDim(Dimension fieldDim) {
            this.fieldDim = fieldDim;
            return this;
        }

        public PacketBuilder board(List<List<Integer>> board) {
            this.board = board;
            return this;
        }

        public PacketBuilder playerInfo(List<List<String>> playerInfo) {
            this.playerInfo = playerInfo;
            return this;
        }

        public PacketBuilder message(String message) {
            this.message = message;
            return this;
        }

        public PacketBuilder startPos(Pos startPos) {
            this.startPos = startPos;
            return this;
        }

        public PacketBuilder endPos(Pos endPos) {
            this.endPos = endPos;
            return this;
        }

        @JsonDeserialize(using = ImageIconDeserializer.class)
        public PacketBuilder image(ImageIcon image) {
            this.image = image;
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
