package com.johnny.petscheduler.exceptions;

public class MissingRefreshTokenException extends RuntimeException
{
    public MissingRefreshTokenException(String message) {
        super(message);
    }

    public MissingRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingRefreshTokenException(Throwable cause) {
        super(cause);
    }
}

