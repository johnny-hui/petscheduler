package com.johnny.petscheduler.exceptions;

public class PetNotFoundException extends RuntimeException
{
    public PetNotFoundException(String message) {
        super(message);
    }

    public PetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PetNotFoundException(Throwable cause) {
        super(cause);
    }
}
