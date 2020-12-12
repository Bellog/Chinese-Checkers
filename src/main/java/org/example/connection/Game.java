package org.example.connection;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final List<Field> board = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final int boardWidth = 3;
    private final String gameVersion;
    private Player current;

    public Game(String gameVersion) {
        this.gameVersion = gameVersion;
        for (var i = 0; i < boardWidth * boardWidth; i++)
            board.add(new Field());
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public synchronized void addPlayer(Player player) {
        if (players.size() >= 2) return;

        if (players.size() == 0) current = player;

        players.add(player);
    }

    private String boardAsText() {
        StringBuilder text = new StringBuilder();
        for (var i = 0; i < boardWidth; i++) {
            for (var j = 0; j < boardWidth; j++) {
                text.append(board.get(i * 3 + j).getMark());
                if (j < boardWidth - 1)
                    text.append("|");
            }
            text.append("\n");
            if (i < boardWidth - 1)
                text.append("------\n");
        }
        return text.toString();
    }


    public synchronized void handleInput(Player player, Packet packet) {
        switch (packet.getCode()) {
            case BOARD_UPDATE -> player.send(new Packet.PacketBuilder().code(Packet.Codes.BOARD_UPDATE).board(boardAsText()).build());
            case PLAYER_MOVE -> move(player, packet.getValue());
        }
    }

    private void move(Player player, int position) {
        System.out.println(player.getMark() + " " + players.indexOf(player));
        if (player != current) {
            player.send(new Packet.PacketBuilder().code(Packet.Codes.WRONG_PLAYER).build());
            return;
        }
        if (board.get(position).getMark().equals(" ")) {
            board.get(position).setMark(player.getMark());
            current = players.get((players.indexOf(player) + 1) % players.size());
            if (hasWinner())
                for (Player p : players)
                    if (p != player)
                        p.send(new Packet.PacketBuilder().code(Packet.Codes.GAME_END).message("you lost!").build());
                    else
                        p.send(new Packet.PacketBuilder().code(Packet.Codes.GAME_END).message("you won!").build());
            else if (isFilledUp())
                for (Player p : players)
                    p.send(new Packet.PacketBuilder().code(Packet.Codes.GAME_END).message("Tie!").build());
            else
                for (Player p : players)
                    if (p != player)
                        p.send(new Packet.PacketBuilder().code(Packet.Codes.OPPONENT_MOVE).board(boardAsText())
                                .message("Opponent " + player.getMark() + " moved").build());
                    else
                        p.send(new Packet.PacketBuilder().code(Packet.Codes.ACTION_SUCCESS).board(boardAsText()).build());
        } else {
            player.send(new Packet.PacketBuilder().code(Packet.Codes.ACTION_FAILURE).message("This field is already set").build());
        }
    }


    private boolean hasWinner() {
        int[] temp = board.stream().mapToInt(v -> ((int) v.getMark().charAt(0))).toArray();

        return (temp[0] != (int) ' ' && temp[0] == temp[1] && temp[0] == temp[2])
               || (temp[3] != (int) ' ' && temp[3] == temp[4] && temp[3] == temp[5])
               || (temp[6] != (int) ' ' && temp[6] == temp[7] && temp[6] == temp[8])
               || (temp[0] != (int) ' ' && temp[0] == temp[3] && temp[0] == temp[6])
               || (temp[1] != (int) ' ' && temp[1] == temp[4] && temp[1] == temp[7])
               || (temp[2] != (int) ' ' && temp[2] == temp[5] && temp[2] == temp[8])
               || (temp[0] != (int) ' ' && temp[0] == temp[4] && temp[0] == temp[8])
               || (temp[2] != (int) ' ' && temp[2] == temp[4] && temp[2] == temp[6]);
    }

    private boolean isFilledUp() {
        return board.stream().noneMatch(v -> v.getMark().equals(" "));
    }
}
