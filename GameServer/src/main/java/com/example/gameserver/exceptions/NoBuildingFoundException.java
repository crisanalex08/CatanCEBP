package com.example.gameserver.exceptions;

public class NoBuildingFoundException extends RuntimeException {
    public NoBuildingFoundException(String message) {
        super("No building found for: " + message);
    }
}
