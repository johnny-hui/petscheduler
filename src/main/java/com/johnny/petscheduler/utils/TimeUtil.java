package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.exceptions.availability.InvalidAvailabilityDateException;

import java.time.*;

/**
 * This utility class provides functions that validates time.
 */
public final class TimeUtil
{
    private static final int APP_TIME_INCREMENT_MINUTES = 15;
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
     * @param clock
     *      The application clock
     *
     * @param requestedDate
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
    public static void validateStartTimeInFutureIfToday(final String timezone,
                                                        final LocalDate requestedDate,
                                                        final LocalTime startTime,
                                                        final Clock clock,
                                                        final RuntimeException exception)
    {
        LocalDate today = TimezoneUtil.getCurrentDate(ZoneId.of(timezone), clock);
        LocalTime now = TimezoneUtil.getCurrentTime(ZoneId.of(timezone), clock);
        if(requestedDate.isEqual(today) && !startTime.isAfter(now)) {
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
        if(time.getMinute() % APP_TIME_INCREMENT_MINUTES != 0
                            || time.getSecond() != 0
                            || time.getNano() != 0)
        {
            throw exception;
        }
    }

    /**
     * Checks if an Availability's date chosen is valid. A date is considered
     * valid if it is not in the past and is within the user's timezone.
     *
     * @throws InvalidAvailabilityDateException
     *      Thrown if the Availability is set in a past date from current date.
     *
     * @param requestedDate
     *      A LocalDate object representing the chosen date for availability
     */
    public static void validateAvailableDateNotInPast(final String timezone,
                                                      final LocalDate requestedDate,
                                                      final Clock clock,
                                                      final RuntimeException exception)
    {
        LocalDate today = TimezoneUtil.getCurrentDate(ZoneId.of(timezone), clock);
        if(requestedDate.isBefore(today)) {
            throw exception;
        }
    }

    /**
     * Checks if the duration of an Availability is greater than or
     * equal to the minimum amount of hours or more.
     *
     * @param startTime
     *      The starting time for an Availability
     *
     * @param endTime
     *      The end time for an Availability
     */
    public static void validateMinimumDuration(final LocalTime startTime,
                                               final LocalTime endTime,
                                               final int minDurationHours,
                                               final RuntimeException exception)
    {
        Duration requestedWindow = Duration.between(startTime, endTime);
        Duration minimumDuration = Duration.ofHours(minDurationHours);

        if(requestedWindow.compareTo(minimumDuration) < 0) {
            throw exception;
        }
    }

    /**
     * Validates whether an Availability's selected date is within max limit of days
     * ahead of current date.
     *
     * @implNote <code>Reason</code>
     *      This is to prevent a service provider from being able to
     *      create Availabilities that are too far in the future
     *
     * @param requestedDate
     *      A LocalDate object containing the chosen date for availability
     *
     * @param availabilityLimitDays
     *      An integer for the max limit of days
     */
    public static void validateWithinMaxFutureDate(final String timezone,
                                                   final LocalDate requestedDate,
                                                   final int availabilityLimitDays,
                                                   final Clock clock,
                                                   final RuntimeException exception)
    {
        LocalDate today = TimezoneUtil.getCurrentDate(ZoneId.of(timezone), clock);
        LocalDate maxDate = today.plusDays(availabilityLimitDays);

        if(requestedDate.isAfter(maxDate)) {
            throw exception;
        }
    }
}