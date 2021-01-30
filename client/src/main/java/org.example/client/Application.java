package org.example.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class Application {

    private ApplicationContext context;

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).headless(false).run();
    }

    @Autowired
    public void context(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            var client = context.getBean(Client.class);
            client.init();
            System.out.println("You can stop client by typing 'exit' or closing window application when available");
            new Thread(() -> { // wait until user types 'exit' then stop client
                var sc = new Scanner(System.in);
                while (sc.hasNext()) {
                    if (sc.next().equals("exit")) {
                        sc.close();
                        System.exit(0);
                        return;
                    }
                }
            }).start();
        };
    }
}
