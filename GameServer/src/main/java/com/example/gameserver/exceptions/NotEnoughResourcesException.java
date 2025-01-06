package com.example.gameserver.exceptions;

public class NotEnoughResourcesException extends RuntimeException {
    public NotEnoughResourcesException() {
        super("Player does not have enough resources to perform this action");
    }
}
