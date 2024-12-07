package com.example.gameserver.exceptions;

public class NullValueException extends RuntimeException {
    public NullValueException(String message) {
        super(message);
    }
}
