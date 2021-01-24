package org.example.server.replay;

import org.example.Pos;
import org.example.server.gameModes.AvailableGameModes;
import org.hibernate.annotations.Cascade;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Service
public class GameSave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Basic(optional = false, fetch = FetchType.EAGER)
    private int players;

    @Transient
    private int rollbackId = 0;

    @Basic(optional = false, fetch = FetchType.EAGER)
    private AvailableGameModes.GameModes mode;

    @OneToMany(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Move> moves = new ArrayList<>();

    public AvailableGameModes.GameModes getMode() {
        return mode;
    }

    public void setGameMode(AvailableGameModes.GameModes mode) {
        this.mode = mode;
    }

    public void commitTurn() {
        rollbackId = moves.size();
    }

    public void rollbackTurn() {
        moves = moves.subList(0, rollbackId);
    }

    public void addMove(Pos start, Pos end, int player) {
        moves.add(new Move(start, end, player));
    }

    public List<Move> getMoves() {
        return moves;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "game " + mode.name() + " for " + players + " players and with " + moves.size() / 2 + " moves";
    }

}
