package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.exceptions.InvalidAvailabilityDateException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This utility class provides functions that validates time.
 */
public final class TimeUtil
{
    private static final int TIME_INCREMENT_MINUTES = 15;
    private TimeUtil(){}

    /**
     * Checks whether startTime is before endTime.
     *
     * @throws InvalidAvailabilityDateException
     *      Thrown if the startTime is after the endTime
     *
     * @param startTime
     *      A LocalTime object
     *
     * @param endTime
     *      A LocalTime object
     *
     * @param exception
     *      Any subclass of RuntimeException
     */
    public static void validateStartBeforeEnd(final LocalTime startTime,
                                              final LocalTime endTime,
                                              final RuntimeException exception)
    {
        if(!startTime.isBefore(endTime)) {
            throw exception;
        }
    }

    /**
     * Checks whether the provided startTime is in the future when the
     * current date is today.
     *
     * @param date
     *      A LocalDate object
     *
     * @param startTime
     *      A LocalTime object
     *
     * @param exception
     *      Any subclass of RuntimeException
     *
     * @throws InvalidAvailabilityDateException
     *      Thrown if start time is not in the future.
     */
    public static void validateStartTimeInFutureIfToday(final LocalDate date,
                                                        final LocalTime startTime,
                                                        final RuntimeException exception)
    {
        Clock clock = Clock.systemDefaultZone();  // gets current time when called
        LocalDate today = LocalDate.now(clock);
        if(date.isEqual(today) && !startTime.isAfter(LocalTime.now(clock))) {
            throw exception;
        }
    }

    /**
     * Checks if the provided time is of 15 minute increments.
     * (e.g. 6:00, 6:15, 6:30, 6:45, 7:00)
     *
     * @param time
     *      A LocalTime object
     *
     * @param exception
     *      Any subclass of RuntimeException
     */
    public static void validateTimeIncrement(final LocalTime time,
                                             final RuntimeException exception)
    {
        if(time.getMinute() % TIME_INCREMENT_MINUTES != 0
                            && time.getSecond() != 0
                            && time.getNano() != 0)
        {
            throw exception;
        }
    }
}