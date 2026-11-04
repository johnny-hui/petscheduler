package com.johnny.petscheduler.exceptions.availability;

public class AvailabilityNotFound extends RuntimeException
{
    public AvailabilityNotFound(String message) {
        super(message);
    }

    public AvailabilityNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public AvailabilityNotFound(Throwable cause) {
        super(cause);
    }
}
