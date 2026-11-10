package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.availability.InvalidAvailabilityDateException;
import com.johnny.petscheduler.exceptions.availability.InvalidAvailabilityTimeException;
import org.springframework.security.access.AccessDeniedException;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.*;
import static com.johnny.petscheduler.utils.TimeUtil.*;

/**
 * Contains utility functions that assist in validating and creating Availabilities.
 */
public final class AvailabilityUtil
{
    private static final int MAX_AVAILABILITY_LIMIT_DAYS = 90;
    private static final int MIN_AVAILABILITY_DURATION_HOURS = 3;

    private AvailabilityUtil(){}

    /**
     * Creates an AvailabilityResponse DTO object.
     *
     * @param availability
     *      An Availability object
     *
     * @return <code>AvailabilityResponseDTO</code>
     *      A record DTO containing relevant information
     */
    public static AvailabilityResponseDTO generateResponse(final Availability availability)
    {
        return new AvailabilityResponseDTO(
                availability.getId(),
                availability.getAvailableDate(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }

    /**
     * Generates a list of AvailabilityResponseDTOs.
     *
     * @param availabilities
     *      A List of Availability objects.
     *
     * @return <code>List of AvailabilityDTOs</code>
     */
    public static List<AvailabilityResponseDTO> generateResponse(final List<Availability> availabilities)
    {
        List<AvailabilityResponseDTO> response = new ArrayList<>();
        if(!availabilities.isEmpty()) {
            for(final Availability availability : availabilities) {
                response.add(generateResponse(availability));
            }
        }
        return response;
    }

    /**
     * Sets information to an Availability object.
     *
     * @param user
     *      A User object (only type PetSitter/PetBusiness)
     *
     * @param request
     *      An AvailabilityRequestDTO object
     *
     * @param availability
     *      An Availability object
     */
    public static void setAvailabilityInfo(final User user,
                                           final AvailabilityRequestDTO request,
                                           Availability availability)
    {
        if(user instanceof PetSitter || user instanceof PetBusiness) {
            availability.setServiceProvider(user);
            availability.setAvailableDate(request.availableDate());
            availability.setStartTime(request.startTime());
            availability.setEndTime(request.endTime());
        }
        throw new AccessDeniedException(ERROR_MESSAGE_ACCESS_DENIED);
    }

    /**
     * Checks if there are any change(s) between an availability and an
     * AvailabilityRequestDTO.
     *
     * @implNote <code>Update(PUT) only</code>
     *      This is only used to perform checks before performing the update
     *
     * @param availability
     *      An Availability object
     *
     * @param request
     *      An AvailabilityRequestDTO object
     *
     * @return <code>bool(T/F)</code>
     *      True if change is present, false otherwise
     */
    public static boolean hasChanged(final Availability availability,
                                     final AvailabilityRequestDTO request)
    {
        return !availability.getAvailableDate().equals(request.availableDate()) ||
               !availability.getStartTime().equals(request.startTime()) ||
               !availability.getEndTime().equals(request.endTime());
    }

    /**
     * Performs a series of business validations on an Availability Request
     * before creating or updating it.
     *
     * @param request
     *      An AvailabilityRequestDTO object
     */
    public static void validateAvailabilityRequest(final User user,
                                                   final AvailabilityRequestDTO request,
                                                   final Clock clock)
            throws InvalidAvailabilityDateException, InvalidAvailabilityTimeException
    {
        // Constraint 1: Ensure the date is not in the past
        validateAvailableDateNotInPast(
                user.getTimezone(),
                request.availableDate(),
                clock,
                new InvalidAvailabilityDateException(ERROR_MESSAGE_INVALID_AVAILABILITY_PAST)
        );

        // Constraint 2: If it's date is TODAY, the start time must be in the future
        validateStartTimeInFutureIfToday(
                user.getTimezone(),
                request.availableDate(),
                request.startTime(),
                clock,
                new InvalidAvailabilityTimeException(ERROR_MESSAGE_START_TIME_TODAY_NOT_IN_FUTURE)
        );

        // Constraint 3: startTime must be before endTime.
        validateStartBeforeEnd(
                request.startTime(),
                request.endTime(),
                new InvalidAvailabilityTimeException(ERROR_MESSAGE_AVAILABILITY_INVALID_START)
        );

        // Constraint 4: Start and end times are of 15 minute increments
        validateTimeIncrement(
                request.startTime(),
                new InvalidAvailabilityTimeException(ERROR_MESSAGE_INVALID_START_TIME_INCREMENT)
        );
        validateTimeIncrement(
                request.endTime(),
                new InvalidAvailabilityTimeException(ERROR_MESSAGE_INVALID_END_TIME_INCREMENT)
        );

        // Constraint 5: Availability duration must be at least 3 hours minimum
        validateMinimumDuration(
                request.startTime(),
                request.endTime(),
                MIN_AVAILABILITY_DURATION_HOURS,
                new InvalidAvailabilityTimeException(
                        ERROR_MESSAGE_AVAILABILITY_MIN_HOURS_NOT_MET +
                        MIN_AVAILABILITY_DURATION_HOURS
                )
        );

        // Constraint 6: Availability must be within 90 days plus current date
        validateWithinMaxFutureDate(
                user.getTimezone(),
                request.availableDate(),
                MAX_AVAILABILITY_LIMIT_DAYS,
                clock,
                new InvalidAvailabilityDateException(
                        ERROR_MESSAGE_AVAILABILITY_PAST_MAX +
                        MAX_AVAILABILITY_LIMIT_DAYS
                )
        );
    }
}