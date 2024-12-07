package com.example.gameserver.exceptions;

public class BuildingIsAlreadyAtMaxLevelException extends RuntimeException {
    public BuildingIsAlreadyAtMaxLevelException() {
        super("Building is already at max level");
    }
}
