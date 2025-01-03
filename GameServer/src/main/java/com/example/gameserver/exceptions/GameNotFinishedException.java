package com.example.gameserver.exceptions;

public class GameNotFinishedException extends RuntimeException {
    public GameNotFinishedException(Long gameId) {
        super("Game with ID " + gameId + " is not finished yet");
    }
}
