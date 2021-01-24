package org.example;

import org.example.client.Client;
import org.example.server.GameHandler;
import org.example.server.Server;
import org.example.server.ServerConnection;
import org.example.server.gameModes.AvailableGameModes;
import org.example.server.replay.GameSave;
import org.example.server.replay.GameSaveRepository;
import org.example.server.replay.Replay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJpaRepositories
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
    public CommandLineRunner start(GameSaveRepository repository) {
        return args -> {
            var sc = new Scanner(System.in);
            System.out.println("Type 'c' to start client, 's' to start server, or 'r' to start replay");
            switch (sc.next()) {
                case "c" -> startClient();
                case "s" -> startServer(sc);
                case "r" -> startReplay(repository, sc);
            }
        };
    }

    private void startClient() {
        var client = context.getBean(Client.class);
        client.init();
    }

    private void startReplay(GameSaveRepository repository, Scanner sc) {
        List<Integer> ids = repository.getAllIds();
        System.out.println("Available game ids: " +
                           ids.stream().map(String::valueOf).collect(Collectors.joining(", ")));

        int id = sc.nextInt();
        if (!ids.contains(id)) {
            System.out.println("Wrong id");
            System.exit(0);
        }
        GameSave save = repository.findById(id).orElse(null);
        if (save == null) {
            System.out.println("Error getting data from database");
            System.exit(0);
        }

        System.out.println("You have selected: " + save.getMode().name() + " for " + save.getPlayers() + " players (" +
                           save.getMoves().size() + " moves)");

        new Thread(() -> {
            var replay = new Replay(save);
            System.out.println("Replay started, close application to stop");
            replay.initGame();
        }).start();
        startClient();
    }

    private void startServer(Scanner sc) {
        System.out.println("Please choose one of available game modes:");
        for (int i = 0; i < AvailableGameModes.GameModes.values().length; i++) {
            System.out.println("\t" + i + ": " + AvailableGameModes.GameModes.values()[i].name());
        }
        AvailableGameModes.GameModes mode = null;
        int players = 0;
        try {
            mode = AvailableGameModes.GameModes.values()[sc.nextInt()];
            if (mode == null)
                throw new Exception();
            System.out.println("Please choose one of these player configuration for chosen game mode:\n\t" +
                               AvailableGameModes.getPlayerNumberList(mode));
            players = sc.nextInt();
            if (players <= 0)
                throw new Exception();
        } catch (Exception e) {
            System.out.println("Incorrect input");
            System.exit(0);
        }

        var server = context.getBean(Server.class);

        var game = AvailableGameModes.getGameMode(mode, players);

        if (game == null) {
            System.out.println("Incorrect player count");
            System.exit(0);
        }
        var save = new GameSave();
        save.setGameMode(mode);
        save.setPlayers(players);
        server.init(new GameHandler(game, server, save), new ServerConnection(server, players));

        new Thread(() -> { // wait until user types 'exit' then stop server
            while (sc.hasNext()) {
                if (sc.next().equals("exit")) {
                    sc.close();
                    server.stop();
                    return;
                }
            }
        }).start();
    }
}
