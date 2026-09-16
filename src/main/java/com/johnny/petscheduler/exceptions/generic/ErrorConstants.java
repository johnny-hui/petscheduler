package com.johnny.petscheduler.exceptions.generic;

public class ErrorConstants {
    // Prevent Instantiation
    private ErrorConstants() {}

    public static final String ERROR_TITLE_INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String ERROR_TITLE_EMAIL_ALREADY_EXISTS = "Email Already Exists";
    public static final String ERROR_TITLE_INVALID_EST_YEAR = "Invalid Business Establishment Year";
    public static final String ERROR_TITLE_USER_NOT_FOUND = "User Not Found";
    public static final String ERROR_TITLE_BAD_VALIDATION = "Data Validation Failed";
    public static final String ERROR_TITLE_BAD_CREDENTIALS = "Bad Credentials";
    public static final String ERROR_TITLE_BAD_ACCOUNT_STATUS = "Bad Account Status";
    public static final String ERROR_TITLE_ACCESS_DENIED = "Access Denied";
    public static final String ERROR_TITLE_INVALID_ENUM_VALUE = "Invalid Enum Value";

    public static final String ERROR_MESSAGE_BAD_VALIDATION = "One or more fields provided has failed validation.";
    public static final String ERROR_MESSAGE_USER_NOT_FOUND = "User with the provided email does not exist!";
    public static final String ERROR_MESSAGE_EMAIL_ALREADY_EXISTS = "An account with the provided email already exists!";
    public static final String ERROR_MESSAGE_INVALID_EST_YEAR = "An invalid business establishment year was provided";
    public static final String ERROR_MESSAGE_BAD_CREDENTIALS = "The provided username or password is incorrect";
    public static final String ERROR_MESSAGE_BAD_ACCOUNT_STATUS = "The account is locked, please contact admin for assistance";
    public static final String ERROR_MESSAGE_ACCESS_DENIED = "You are not authorized to access this resource";
}
