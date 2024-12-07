package com.example.gameserver.exceptions;

public class NoResourceFoundException extends RuntimeException {
    public NoResourceFoundException(String message) {
        super("No building found for: " + message);
    }
}
