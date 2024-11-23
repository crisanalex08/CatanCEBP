package com.example.gameserver.exceptions;

public class HostAlreadyJoinedException extends RuntimeException {
    public HostAlreadyJoinedException(Long gameId) {
        super("Host already joined the game: " + gameId);
    }

}
