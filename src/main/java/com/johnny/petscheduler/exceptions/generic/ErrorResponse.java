package com.johnny.petscheduler.exceptions.generic;

import java.time.Instant;
import java.util.Map;

/**
 * This record is used to encapsulate data for any exceptions thrown.
 *
 * @param status
 *      An integer for the HTTP status code
 *
 * @param errorType
 *      A value from the ErrorType enum
 *
 * @param errorTitle
 *      A String for the name of the error
 *
 * @param errorMessage
 *      A String for the error message
 *
 * @param errorFields
 *      A Map containing key/value pairs for field:exception message
 *
 * @param timestamp
 *      The timestamp of when the exception has occurred
 */
public record ErrorResponse(
        int status,
        ErrorCode errorType,
        String errorTitle,
        String errorMessage,
        Map<String, String> errorFields,
        Instant timestamp
) {}
