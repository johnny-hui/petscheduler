package com.johnny.petscheduler.exceptions;

public class PetBulkCreateEmptyException extends RuntimeException {
    public PetBulkCreateEmptyException(String message) {
        super(message);
    }

    public PetBulkCreateEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PetBulkCreateEmptyException(Throwable cause) {
        super(cause);
    }
}
