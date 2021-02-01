import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.connection.Packet;
import org.example.connection.Pos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This checks whether serialization of {@link org.example.connection.Packet} works properly
 */
public class SerializationTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testSerialize() throws IOException {
        Packet.Codes code = Packet.Codes.GAME_SETUP;
        List<List<Integer>> board = new ArrayList<>();
        List<List<String>> playerInfo = new ArrayList<>();
        int playerId = 3;
        Dimension fieldDim = new Dimension(4, 5);
        String message = "message";
        Pos startPos = new Pos(1, 2);
        Pos endPos = new Pos(3, 4);

        for (int i = 0; i < 7; i++) {
            board.add(new ArrayList<>());
            playerInfo.add(new ArrayList<>());
            for (int j = 0; j < i + 1; j++) {
                board.get(i).add(j);
                playerInfo.get(i).add(j + "s");
            }
        }

        Packet deserialized = convert(new Packet.PacketBuilder()
                .code(code).board(board).playerInfo(playerInfo).playerId(playerId).fieldDim(fieldDim)
                .message(message).startPos(startPos).endPos(endPos)
                .build());

        assertEquals(code, deserialized.getCode());
        assertEquals(board, deserialized.getBoard());
        assertEquals(playerInfo, deserialized.getPlayerInfo());
        assertEquals(fieldDim, deserialized.getFieldDim());
        assertEquals(message, deserialized.getMessage());
        assertEquals(startPos, deserialized.getStartPos());
        assertEquals(endPos, deserialized.getEndPos());
    }

    @Test
    public void testSerializeImage() throws IOException {
        BufferedImage image = new BufferedImage(150, 200, BufferedImage.TYPE_3BYTE_BGR);
        image.getGraphics().drawLine(0, 0, 75, 23); // make sure image is not empty

        var icon = new ImageIcon(image);

        Packet deserialized = convert(new Packet.PacketBuilder()
                .image(icon).build());

        assertEquals(icon.getIconWidth(), deserialized.getImage().getIconWidth());
        assertEquals(icon.getIconHeight(), deserialized.getImage().getIconHeight());
    }

    @Test
    public void testSerializeColor() throws IOException {
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < 7; i++)
            colors.add(new Color(i));

        Packet deserialized = convert(new Packet.PacketBuilder().colors(colors).build());

        for (int i = 0; i < colors.size(); i++)
            assertEquals(colors.get(i).getRGB(), deserialized.getColors().get(i).getRGB());
    }

    /**
     * serializes and deserializes packet
     *
     * @param packet packet to serialize
     * @return returns deserialized packet
     * @throws IOException if serialization or deserialization fails
     */
    private Packet convert(Packet packet) throws IOException {
        String serialized = mapper.writer().writeValueAsString(packet);
        return mapper.reader().readValue(serialized, Packet.class);
    }
}
