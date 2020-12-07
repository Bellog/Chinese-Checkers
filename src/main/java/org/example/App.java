package org.example;

import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.print("type something: ");
        var s = new Scanner(System.in);
        switch (s.next()) {
            case "d" -> System.out.println("xD");
            case "x" -> System.out.println("XD");
            default -> System.out.println("test");
        }
    }
}
