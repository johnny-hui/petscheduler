package com.johnny.petscheduler.exceptions.availability;

/**
 * Thrown when an Availability request is overlapping with one or more existing ones.
 */
public class AvailabilityOverlapException extends RuntimeException
{
    public AvailabilityOverlapException(String message) {
        super(message);
    }

    public AvailabilityOverlapException(String message, Throwable cause) {
        super(message, cause);
    }

    public AvailabilityOverlapException(Throwable cause) {
        super(cause);
    }
}
