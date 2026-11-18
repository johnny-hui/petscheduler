package com.johnny.petscheduler.exceptions.availability;

/**
 * Thrown when an Availability request fails validation on the date.
 */
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
