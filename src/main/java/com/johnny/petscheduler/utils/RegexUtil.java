package com.johnny.petscheduler.utils;

/**
 * A utility class that provides regex patterns for data input validations.
 */
public final class RegexUtil
{
    // Prevent object instantiation
    private RegexUtil() {}

    public static final String COUNTRY_CODE = "^[A-Z]{2}$";
    public static final String CITY_PATTERN = "^[\\p{L}\\p{M} .'-]+$";
    public static final String PASSWORD_CHECK = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$";
    public static final String PHONE_NUMBER = "^(?:\\+1[-.\\s]?)?(?:\\(\\d{3}\\)|\\d{3})[-.\\s]?\\d{3}[-.\\s]?\\d{4}$";
    public static final String ALPHABET_ONLY = "^[A-Za-z]+(?: [A-Za-z]+)*$";
}
