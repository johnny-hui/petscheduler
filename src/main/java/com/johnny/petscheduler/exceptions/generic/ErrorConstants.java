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
    public static final String ERROR_TITLE_PET_COUNT_EXCEEDED = "Pet Count Exceeded";
    public static final String ERROR_TITLE_PET_NOT_FOUND = "Pet Not Found";
    public static final String ERROR_TITLE_PET_GET = "GET PET ERROR: ";
    public static final String ERROR_TITLE_PET_UPDATE = "UPDATE PET ERROR: ";
    public static final String ERROR_TITLE_PET_DELETE = "DELETE PET ERROR: ";
    public static final String ERROR_TITLE_PET_CREATE = "CREATE PET ERROR: ";
    public static final String ERROR_TITLE_USER_PROFILE_ERROR = "User Profile Error: ";

    public static final String ERROR_MESSAGE_BAD_VALIDATION = "One or more fields provided has failed validation.";
    public static final String ERROR_MESSAGE_USER_NOT_FOUND = "User with the provided email does not exist!";
    public static final String ERROR_MESSAGE_GENERAL_USER_NOT_FOUND = "The user requested cannot be found or does not exist!";
    public static final String ERROR_MESSAGE_EMAIL_ALREADY_EXISTS = "An account with the provided email already exists!";
    public static final String ERROR_MESSAGE_INVALID_EST_YEAR = "An invalid business establishment year was provided";
    public static final String ERROR_MESSAGE_BAD_CREDENTIALS = "The provided username or password is incorrect";
    public static final String ERROR_MESSAGE_BAD_ACCOUNT_STATUS = "The account is locked, please contact admin for assistance";
    public static final String ERROR_MESSAGE_ACCESS_DENIED = "You are not authorized to access this resource";
    public static final String ERROR_MESSAGE_PET_COUNT_EXCEEDED = "You have already reached max limit of pets registered";
    public static final String ERROR_MESSAGE_PET_FETCH = "The pet that was requested does not exist or you lack access to this resource";
    public static final String ERROR_MESSAGE_PET_BULK_CREATE_EMPTY = "No pets were provided.";
    public static final String ERROR_MESSAGE_PET_BULK_CREATE_LIMIT_PROVIDED = "You have provided more pets for registration than the max limit of pets allowed";
    public static final String ERROR_MESSAGE_PET_BULK_CREATE_LIMIT_EXCEEDED = "You cannot register more pets than the max limit of pets allowed";
    public static final String ERROR_MESSAGE_USER_PROFILE_ERROR= "The requested user profile cannot be loaded at this time";
}
