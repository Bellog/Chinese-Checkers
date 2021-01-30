package org.example.connection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Converts ImageIcon object to Base64 MIME String and appends it to jsonGenerator
 */
public class ImageIconSerializer extends JsonSerializer<ImageIcon> {
    @Override
    public void serialize(ImageIcon imageIcon, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // converting ImageIcon requires using BufferedImage which in turn requires Image
        var stream = new ByteArrayOutputStream();
        var image = imageIcon.getImage();
        var bufferedImage = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        ImageIO.write(bufferedImage, "JPG", stream);

        jsonGenerator.writeString(Base64.getEncoder().encodeToString(stream.toByteArray()));
    }
}
