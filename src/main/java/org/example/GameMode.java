package org.example;

import java.util.Arrays;
import java.util.List;

public class GameMode {

    public static List<Modes> getModes() {
        return Arrays.asList(Modes.class.getEnumConstants());

    }

    public static ARuleSet getRuleSet(Modes mode) {
        return switch (mode) {
            case basic -> new BasicRuleSet();
            case standard -> null;
        };
    }

    public enum Modes {
        basic,
        standard
    }
}
