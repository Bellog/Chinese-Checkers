package org.example.connection;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.awt.*;
import java.io.IOException;

public class ColorDeserializer extends JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return new Color(jsonParser.getValueAsInt());
    }
}
