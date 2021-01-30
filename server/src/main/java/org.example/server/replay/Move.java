package org.example.server.replay;

import org.example.connection.Pos;

import javax.persistence.*;

@Entity
public class Move {

    @Basic
    private final Pos start;
    @Basic
    private final Pos end;
    @Basic
    private final int player;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public Move(Pos start, Pos end, int player) {
        this.start = start;
        this.end = end;
        this.player = player;
    }

    public Move() {
        start = null;
        end = null;
        player = -1;
    }

    public Pos getStart() {
        return start;
    }

    public Pos getEnd() {
        return end;
    }

    public int getPlayer() {
        return player;
    }
}
