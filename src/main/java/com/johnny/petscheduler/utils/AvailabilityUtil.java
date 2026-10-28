package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.exceptions.InvalidAvailabilityDateException;
import com.johnny.petscheduler.exceptions.InvalidAvailabilityTimeException;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.*;

/**
 * Contains utility functions that assist in validating and creating Availabilities.
 */
public final class AvailabilityUtil
{
    private static final int MAX_AVAILABILITY_LIMIT_DAYS = 90;
    private static final int MIN_AVAILABILITY_DURATION_HOURS = 3;

    private AvailabilityUtil(){}

    /**
     * Checks if an Availability's date chosen is valid. A date is considered
     * valid if it is not before the current day's date.
     *
     * @throws InvalidAvailabilityDateException
     *      Thrown if the Availability is set in a past date from current date.
     *
     * @param availableDate
     *      A LocalDate object representing the chosen date for availability
     */
    public static void validateAvailableDate(final LocalDate availableDate)
            throws InvalidAvailabilityDateException
    {
        LocalDate today = LocalDate.now();
        if(availableDate.isBefore(today)) {
            throw new InvalidAvailabilityDateException(ERROR_MESSAGE_INVALID_AVAILABILITY_PAST);
        }
    }

    /**
     * Validates whether an Availability's selected date is
     * within the max limit.
     *
     * @implNote <code>Reason</code>
     *      This is to prevent a service provider from being able to
     *      create Availabilities that are too far from current date
     *
     * @param availableDate
     *      A LocalDate object containing the chosen date for availability
     */
    public static void validateWithinMaxFutureDate(final LocalDate availableDate)
    {
        Clock clock = Clock.systemDefaultZone();
        LocalDate today = LocalDate.now(clock);
        LocalDate maxDate = today.plusDays(MAX_AVAILABILITY_LIMIT_DAYS);

        if(availableDate.isAfter(maxDate)) {
            throw new InvalidAvailabilityDateException(
                    ERROR_MESSAGE_AVAILABILITY_PAST_MAX
                    + MAX_AVAILABILITY_LIMIT_DAYS
            );
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
                                                  final LocalTime endTime)
    {
        Duration requestedWindow = Duration.between(startTime, endTime);
        Duration minimumDuration = Duration.ofHours(MIN_AVAILABILITY_DURATION_HOURS);

        if(requestedWindow.compareTo(minimumDuration) < 0) {
            throw new InvalidAvailabilityTimeException(
                    ERROR_MESSAGE_AVAILABILITY_MIN_HOURS_NOT_MET + MIN_AVAILABILITY_DURATION_HOURS
            );
        }
    }
}