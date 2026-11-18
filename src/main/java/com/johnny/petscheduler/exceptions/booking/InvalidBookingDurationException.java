package com.johnny.petscheduler.exceptions.booking;

public class InvalidBookingDurationException extends RuntimeException
{
    public InvalidBookingDurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBookingDurationException(Throwable cause) {
        super(cause);
    }

    public InvalidBookingDurationException(String message) {
        super(message);
    }
}
