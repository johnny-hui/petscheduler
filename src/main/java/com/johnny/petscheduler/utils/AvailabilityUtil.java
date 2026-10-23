package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.exceptions.InvalidAvailabilityDateException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.*;

/**
 * Contains utility functions that assist in validating Availabilities.
 */
public final class AvailabilityUtil
{
    private AvailabilityUtil(){}

    /**
     * Checks if an Availability is in the past.
     *
     * @throws InvalidAvailabilityDateException
     *      Thrown if the Availability is set in a past date from current date.
     *
     * @param request
     *      An AvailabilityRequestDTO object
     *
     * @return <code>boolean(T/F)</code>
     *      False if date not is in the past, InvalidAvailabilityDateException if true
     */
    public static boolean isAvailableDateInPast(final AvailabilityRequestDTO request)
            throws InvalidAvailabilityDateException
    {
        LocalDate today = LocalDate.now();
        if(request.availableDate().isBefore(today)) {
            throw new InvalidAvailabilityDateException(ERROR_MESSAGE_INVALID_AVAILABILITY_PAST);
        }
        return false;
    }

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
     * @return <code>boolean(T/F)</code>
     *      True if start is before end; InvalidAvailabilityDateException otherwise
     */
    public static boolean isStartBeforeEnd(final LocalTime startTime,
                                           final LocalTime endTime) throws InvalidAvailabilityDateException
    {
        if(startTime.isBefore(endTime)) {
            return true;
        }
        throw new InvalidAvailabilityDateException(ERROR_MESSAGE_AVAILABILITY_START_AFTER_END);
    }

    /**
     * Checks whether an Availability's startTime is in the future when the
     * current date is today.
     *
     * @param date
     *      A LocalDate object
     *
     * @param startTime
     *      A LocalTime object
     *
     * @return <code>boolean(T/F)</code>
     *      True if start time is in the future, InvalidAvailabilityDateException otherwise
     *
     * @throws InvalidAvailabilityDateException
     *      Thrown if start time is not in the future.
     */
    public static boolean isStartTimeInFutureIfToday(final LocalDate date,
                                                     final LocalTime startTime) throws InvalidAvailabilityDateException
    {
        Clock clock = Clock.systemDefaultZone();
        LocalDate today = LocalDate.now(clock);
        if (date.isEqual(today) && !startTime.isAfter(LocalTime.now(clock))) {
            throw new InvalidAvailabilityDateException(ERROR_MESSAGE_START_TIME_TODAY_NOT_IN_FUTURE);
        }
        return true;
    }
}
