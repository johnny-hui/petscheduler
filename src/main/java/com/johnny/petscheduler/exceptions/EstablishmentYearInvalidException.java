package com.johnny.petscheduler.exceptions;

public class EstablishmentYearInvalidException extends RuntimeException {
    public EstablishmentYearInvalidException(String message) {
        super(message);
    }

    public EstablishmentYearInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public EstablishmentYearInvalidException(Throwable cause) {
        super(cause);
    }
}
