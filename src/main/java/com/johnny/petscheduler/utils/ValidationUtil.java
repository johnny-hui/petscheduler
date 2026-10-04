package com.johnny.petscheduler.utils;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.Year;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
     * during JPA method-level validation.
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

    /**
     * Parses field names and validation messages from exceptions generated
     * in JPA controller-method parameters such as List<@Valid SomeDTO>.
     *
     * @param ex
     *      A HandlerMethodValidationException object
     *
     * @return errors
     */
    public static Map<String, String> parseJPAValidationErrors(final HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getParameterValidationResults().forEach(result -> {

            String parameterName = result.getMethodParameter().getParameterName();

            result.getResolvableErrors().forEach(error -> {
                String message = error.getDefaultMessage();
                errors.put(parameterName, message);
            });
        });

        return errors;
    }

    /**
     * Parses for any errors and the fields affected from Jackson binding when
     * invalid enums are provided in a client DTO object.
     *
     * @param ex
     *      An InvalidFormatException object
     *
     * @return message
     *      The parsed error with fields
     */
    public static String parseJacksonInvalidEnumErrors(final InvalidFormatException ex) {
        String invalidValue = String.valueOf(ex.getValue());

        String fieldName = ex.getPath().isEmpty()
                ? "field"
                : ex.getPath()
                    .getLast()
                    .getPropertyName();

        String allowedValues = Arrays.stream(ex.getTargetType().getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        return String.format(
                "Invalid value '%s' for field '%s'. Allowed values: %s",
                invalidValue,
                fieldName,
                allowedValues
        );
    }
}
