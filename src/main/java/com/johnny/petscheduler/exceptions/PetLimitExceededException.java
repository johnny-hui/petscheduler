package com.johnny.petscheduler.exceptions;

public class PetLimitExceededException extends RuntimeException
{
    public PetLimitExceededException(String message) {
        super(message);
    }

    public PetLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public PetLimitExceededException(Throwable cause) {
        super(cause);
    }
}
