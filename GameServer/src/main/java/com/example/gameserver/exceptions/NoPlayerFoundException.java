package com.example.gameserver.exceptions;

public class NoPlayerFoundException extends RuntimeException {
    public NoPlayerFoundException(String s) {
        super("No player found for the game: " + s);
    }

}
