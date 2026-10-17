package com.johnny.petscheduler.exceptions;

public class InvalidJWTException extends RuntimeException
{
    public InvalidJWTException(String message) {
        super(message);
    }

    public InvalidJWTException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidJWTException(Throwable cause) {
        super(cause);
    }
}
