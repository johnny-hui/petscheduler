package com.johnny.petscheduler.exceptions.availability;

/**
 * Thrown when an Availability request fails validation on either start or end time.
 */
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
