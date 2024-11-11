package com.example.gameserver.aggregates;

import lombok.Data;

@Data
public class Position {
    private final int y;
    private final int x;
    public Position(int x, int y) {
        this.y = y;
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
