package com.johnny.petscheduler.exceptions;

public class InvalidUserRoleException extends RuntimeException
{
    public InvalidUserRoleException(String message) {
        super(message);
    }
    public InvalidUserRoleException(Throwable cause) {
        super(cause);
    }
    public InvalidUserRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
