package com.johnny.petscheduler.exceptions;

public class UserProfileCannotBeLoadedException extends RuntimeException
{
    public UserProfileCannotBeLoadedException(String message) {
        super(message);
    }

    public UserProfileCannotBeLoadedException(Throwable cause) {
        super(cause);
    }

    public UserProfileCannotBeLoadedException(String message, Throwable cause) {
        super(message, cause);
    }
}
