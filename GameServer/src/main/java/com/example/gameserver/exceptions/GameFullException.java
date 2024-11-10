package com.example.gameserver.exceptions;

public class GameFullException extends RuntimeException {
    public GameFullException(String gameId) {
        super("Game is full: " + gameId);
    }
}