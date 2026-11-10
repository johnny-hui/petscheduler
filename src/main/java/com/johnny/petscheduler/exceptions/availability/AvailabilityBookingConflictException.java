package com.johnny.petscheduler.exceptions.availability;

public class AvailabilityBookingConflictException extends RuntimeException
{
    public AvailabilityBookingConflictException(String message) {
        super(message);
    }
    
    public AvailabilityBookingConflictException(Throwable cause) {
        super(cause);
    }

    public AvailabilityBookingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
