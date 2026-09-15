package com.johnny.petscheduler.utils;

/**
 * A utility class that provides regex patterns for data input validations.
 */
public final class RegexUtil
{
    // Prevent object instantiation
    private RegexUtil() {};

    public static final String CANADIAN_POSTAL_CODE = "^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d$";
    public static final String PASSWORD_CHECK = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$";
    public static final String PHONE_NUMBER = "^(?:\\+1[-.\\s]?)?(?:\\(\\d{3}\\)|\\d{3})[-.\\s]?\\d{3}[-.\\s]?\\d{4}$";
}
