package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Helper class for easier json serialization and deserialization
 */
public class JsonUtils {
    public static final ObjectMapper json;

    static {
        json = (new ObjectMapper()).configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
    }

    public static ObjectMapper getMapper() {
        return json;
    }

    public static String toJson(Object object) {
        try {
            return json.writer().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Error parsing object of " + object.getClass().getName() + " type to Json ");
        }
        return "";
    }

    public static <T> T fromJson(String object, Class<T> type) {
        try {
            return json.reader().readValue(object, type);
        } catch (IOException e) {
            System.out.println("Error parsing object of type " + type.getName() + " from Json");
        }
        return null;
    }
}