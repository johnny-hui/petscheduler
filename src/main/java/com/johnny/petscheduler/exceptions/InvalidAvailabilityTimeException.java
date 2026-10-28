package com.johnny.petscheduler.exceptions;

public class InvalidAvailabilityTimeException extends RuntimeException {
    public InvalidAvailabilityTimeException(String message) {
        super(message);
    }

    public InvalidAvailabilityTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAvailabilityTimeException(Throwable cause) {
        super(cause);
    }
}
