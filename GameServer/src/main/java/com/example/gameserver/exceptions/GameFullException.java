package com.example.gameserver.exceptions;

public class GameFullException extends RuntimeException {
    public GameFullException(Long gameId) {
        super("Game is full: " + gameId);
    }
}