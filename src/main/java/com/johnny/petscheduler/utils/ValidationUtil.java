package com.johnny.petscheduler.utils;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class that provides methods for validating various data.
 */
public class ValidationUtil
{
    // Prevent Instantiation
    private ValidationUtil() {}

    /**
     * Validates the value of the business year established.
     *
     * @param year
     *      An integer representing the year established
     *
     * @return Boolean (T/F)
     *      True if valid; false otherwise
     */
    public static boolean isYearEstablishedValid(final Integer year) {
        if (year == null) {
            return false;
        }
        int currentYear = Year.now().getValue();
        return (year >= 1800 && year <= currentYear);
    }

    /**
     * Parses field names and validation messages from exceptions generated
     * during JPA validation.
     *
     * @param ex
     *     A MethodArgumentNotValidException object
     *
     * @return Map<String, String>
     *     A Map containing key/value pairs of field/message
     */
    public static Map<String, String> parseJPAValidationErrors(final MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return errors;
    }
}
