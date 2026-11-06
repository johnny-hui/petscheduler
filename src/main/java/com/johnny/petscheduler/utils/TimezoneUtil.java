package com.johnny.petscheduler.utils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

/**
 * This utility class contains methods to validate the timezones
 * perform time and date conversions.
 */
public class TimezoneUtil
{
    private static final Set<String> VALID_TIMEZONES = ZoneId.getAvailableZoneIds();

    // Prevent Instantiation
    private TimezoneUtil() {}

    /**
     * Validates whether a timezone is valid.
     *
     * @param timezone
     *      A String containing the timezone
     */
    public static boolean isValidTimezone(final String timezone) {
        return timezone != null && VALID_TIMEZONES.contains(timezone);
    }

    /**
     * Returns the current date after performing clock and timezone conversion.
     *
     * @param timezone
     *      A String for the timezone
     *
     * @param clock
     *      The injected application Clock
     *
     * @return <code>LocalDate</code>
     *      The current date
     */
    public static LocalDate getCurrentDate(final ZoneId timezone,
                                           final Clock clock)
    {

        return LocalDate.now(clock.withZone(timezone));
    }

    /**
     * Returns the current time after performing timezone conversion.
     *
     * @param timezone
     *      A String for the timezone
     *
     * @param clock
     *      The injected application Clock
     *
     * @return <code>LocalTime</code>
     *      The current date
     */
    public static LocalTime getCurrentTime(final ZoneId timezone,
                                           final Clock clock)
    {
        return LocalTime.now(clock.withZone(timezone));
    }
}
