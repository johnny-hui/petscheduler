package com.johnny.petscheduler.exceptions;

public class InvalidAvailabilityDateException extends RuntimeException
{
    public InvalidAvailabilityDateException(String message) {
        super(message);
    }

    public InvalidAvailabilityDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAvailabilityDateException(Throwable cause) {
        super(cause);
    }
}
