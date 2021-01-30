package org.example.connection;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Converts Base64 MIME encoded image to ImageIcon and returns it
 */
public class ImageIconDeserializer extends JsonDeserializer<ImageIcon> {
    @Override
    public ImageIcon deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {

        var decoded = Base64.getDecoder().decode(jsonParser.getValueAsString());
        var stream = new ByteArrayInputStream(decoded);
        return new ImageIcon(ImageIO.read(stream));
    }
}
