package com.johnny.petscheduler.exceptions;

public class InvalidServiceRateException extends RuntimeException
{
    public InvalidServiceRateException(String message) {
        super(message);
    }
    public InvalidServiceRateException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidServiceRateException(Throwable cause) {
        super(cause);
    }
}
