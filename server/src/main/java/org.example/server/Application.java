package org.example.server;

import org.example.server.gameModes.AvailableGameModes;
import org.example.server.replay.GameSave;
import org.example.server.replay.GameSaveRepository;
import org.example.server.replay.ReplayGameHandler;
import org.example.server.replay.ReplayServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * To run this application you need to execute it with profile, see resources folder for available profiles
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class Application {

    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Autowired
    public void context(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public CommandLineRunner start(GameSaveRepository repository) {
        return args -> {
            if (context.getBean(IServer.class) instanceof ReplayServer) {
                startReplay(repository, new Scanner(System.in));
            } else {
                startServer();
            }
        };
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
            var server = context.getBean(ReplayServer.class);
            var mode = AvailableGameModes.getGameMode(save.getMode(), save.getPlayers());
            server.setGameHandler(new ReplayGameHandler(mode, server));
            server.setReplay(save);
            System.out.println("Join the game with a client to start replay");
            exitHandler(sc, server);
        }).start();
    }

    private void startServer() {
        var sc = new Scanner(System.in);
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
        server.setGameHandler(new GameHandler(game, server, save));
        System.out.println("Server started");

        exitHandler(sc, server);
    }

    private void exitHandler(Scanner sc, Server server) {
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
