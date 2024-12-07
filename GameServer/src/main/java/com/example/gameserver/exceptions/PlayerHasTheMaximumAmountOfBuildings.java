package com.example.gameserver.exceptions;

public class PlayerHasTheMaximumAmountOfBuildings extends RuntimeException {
    public PlayerHasTheMaximumAmountOfBuildings() {
        super("Player already has 3 buildings");
    }
}
