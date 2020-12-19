package org.example.server;

public class Field {

    private String mark = " ";
    private volatile int state = -1;
    private final int base;

    public Field(int base) {
        this.base = base;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getState() { return state; }

    public void setState(int state) { this.state = state; }

    public int getBase() { return base; }

}
