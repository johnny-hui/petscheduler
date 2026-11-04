package com.johnny.petscheduler.utils;

import java.time.ZoneId;
import java.util.Set;

/**
 * This utility class contains methods that validate the timezone.
 */
public class TimezoneUtil
{
    private static final Set<String> VALID_TIMEZONES = ZoneId.getAvailableZoneIds();

    // Prevent Instantiation
    private TimezoneUtil() {}

    public static boolean isValidTimezone(final String timezone) {
        return timezone != null && VALID_TIMEZONES.contains(timezone);
    }
}
