package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.AvailabilityDAO;
import com.johnny.petscheduler.DAO.BookingDAO;
import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.InvalidUserRoleException;
import com.johnny.petscheduler.exceptions.UserNotFoundException;
import com.johnny.petscheduler.exceptions.availability.*;
import com.johnny.petscheduler.utils.AvailabilityUtil;
import com.johnny.petscheduler.utils.TimezoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.*;
import static com.johnny.petscheduler.utils.TimeUtil.*;

@Service
public class AvailabilityService
{
    private static final String PAST = "past";
    private static final String CURRENT = "current";
    private static final int QUERY_PAGE_SIZE_MIN = 1;
    private static final int QUERY_PAGE_SIZE_MAX = 100;

    private final AvailabilityDAO availabilityDAO;
    private final BookingDAO bookingDAO;
    private final UserDAO userDAO;
    private final Clock clock;

    @Autowired
    public AvailabilityService(final AvailabilityDAO availabilityDAO,
                               final BookingDAO bookingDAO,
                               final UserDAO userDAO,
                               final Clock clock)
    {
        this.availabilityDAO = availabilityDAO;
        this.bookingDAO = bookingDAO;
        this.userDAO = userDAO;
        this.clock = clock;
    }

    // Read (GET)
    /**
     * Returns information on a specific Availability.
     *
     * @param availabilityID
     *      A Long for the availability ID
     *
     * @return <code>AvailabilityResponseDTO</code>
     *      A record containing Availability information
     */
    @Transactional(readOnly = true)
    public AvailabilityResponseDTO getAvailability(final Long availabilityID)
    {
        Availability availability = availabilityDAO.getAvailabilityById(availabilityID)
                .orElseThrow(
                        () -> new AvailabilityNotFoundException(ERROR_MESSAGE_AVAILABILITY_NOT_FOUND)
                );
        return AvailabilityUtil.generateResponse(availability);
    }

    /**
     * Returns a list of current and future Availabilities owned by a
     * PetSitter or PetBusiness based on the user's timezone.
     *
     * @param authentication
     *      An Authentication object
     *
     * @return <code>A List of Availabilities</code>
     */
    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getAvailabilities(final Long userID,
                                                           final Authentication authentication)
    {
        User user = getUserContext(userID, authentication);

        // Get all current & upcoming availabilities from DB (within user's timezone)
        if(user instanceof PetSitter || user instanceof PetBusiness) {
            return generatePastOrCurrentAvailabilities(user, CURRENT);
        }
        throw new InvalidUserRoleException(ERROR_MESSAGE_USER_NOT_PROVIDER);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getPreviousAvailabilities(final Long userID,
                                                                   final Authentication authentication)
    {
        User user = getUserContext(userID, authentication);

        // Get all previous availabilities from DB (within user's timezone)
        if(user instanceof PetSitter || user instanceof PetBusiness) {
            return generatePastOrCurrentAvailabilities(user, PAST);
        }
        throw new InvalidUserRoleException(ERROR_MESSAGE_USER_NOT_PROVIDER);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getAvailabilitiesByQuery(final LocalDate requestedDate,
                                                                  final LocalTime startTime,
                                                                  final LocalTime endTime,
                                                                  final int page, final int size,
                                                                  final Authentication authentication)
    {
        // Validate the Query
        User user = (User) authentication.getPrincipal();
        validateQuery(
                user.getTimezone(), requestedDate, startTime,
                endTime, clock, page, size
        );

        // Execute the Query
        List<Availability> results = availabilityDAO.getAvailabilitiesByDateAndTime(
                requestedDate, startTime, endTime, page, size
        );

        return AvailabilityUtil.generateResponse(results);
    }

    // Create (POST)
    @Transactional
    public AvailabilityResponseDTO createAvailability(final AvailabilityRequestDTO request,
                                                      final Authentication authentication)
    {
        // Get User object after passing Spring Authentication
        User user = (User) authentication.getPrincipal();

        // Perform user role check and validate the Availability request
        if (user instanceof PetSitter || user instanceof PetBusiness) {
            AvailabilityUtil.validateAvailabilityRequest(user, request, clock);
            Availability availability = validateOverlapAndPersistAvailability(user, request);
            return AvailabilityUtil.generateResponse(availability);
        }
        throw new AccessDeniedException(ERROR_MESSAGE_ACCESS_DENIED);
    }

    @Transactional
    public List<AvailabilityResponseDTO> createBulkAvailabilities(final List<AvailabilityRequestDTO> requests,
                                                                  final Authentication authentication)
    {
        // Get User object after passing Spring Authentication
        User user = (User) authentication.getPrincipal();

        // Validate each availability and perform batch persistence
        if(user instanceof PetSitter || user instanceof PetBusiness) {
            List<AvailabilityResponseDTO> availabilities = new ArrayList<>();

            for (final AvailabilityRequestDTO request : requests) {
                AvailabilityUtil.validateAvailabilityRequest(user, request, clock);
                Availability availability = validateOverlapAndPersistAvailability(user, request);
                availabilities.add(AvailabilityUtil.generateResponse(availability));
            }
            return availabilities;
        }
        throw new AccessDeniedException(ERROR_MESSAGE_ACCESS_DENIED);
    }

    // Update (PUT)
    @Transactional
    public AvailabilityResponseDTO updateAvailability(final Long availabilityID,
                                                      final AvailabilityRequestDTO request,
                                                      final Authentication authentication)
    {
        User user = (User) authentication.getPrincipal();

        if (user instanceof PetSitter || user instanceof PetBusiness) {
            // Constraint 1: Verify the existence of the Availability and ownership
            Availability availability = availabilityDAO
                    .getAvailabilityByIdAndProviderId(availabilityID, user.getId())
                    .orElseThrow(() -> new AvailabilityNotFoundException(ERROR_MESSAGE_AVAILABILITY_NOT_FOUND));

            // Constraint 2: Check if anything is changing b/w request and the availability
            if(!AvailabilityUtil.hasChanged(availability, request)) {
                return AvailabilityUtil.generateResponse(availability);
            }

            // Constraint 3: Perform request validation
            AvailabilityUtil.validateAvailabilityRequest(user, request, clock);

            // Constraint 4: Check for existing bookings in current availability's window
            if(!bookingDAO.anyActiveBookings(  // only status REQUESTED, CONFIRMED
                    user.getId(),
                    availability.getAvailableDate(),
                    availability.getStartTime(),
                    availability.getEndTime()))
            {
                // Constraint 5: Check for overlaps and perform update
                validateOverlapAndUpdateAvailability(user, request, availability);
                return AvailabilityUtil.generateResponse(availability);
            }
            throw new AvailabilityBookingConflictException(
                    ERROR_MESSAGE_UPDATE_AVAILABILITY_BOOKING_CONFLICT
            );
        }
        throw new AccessDeniedException(ERROR_MESSAGE_ACCESS_DENIED);
    }

    // Delete (DELETE)
    @Transactional
    public ResponseEntity<Void> deleteAvailability(final Long availabilityID,
                                                   final Authentication authentication)
    {
        User user = (User) authentication.getPrincipal();

        if(user instanceof PetSitter || user instanceof PetBusiness) {
            // 1. Verify the existence of the Availability and ownership
            Availability availability = availabilityDAO
                    .getAvailabilityByIdAndProviderId(availabilityID, user.getId())
                    .orElseThrow(() -> new AvailabilityNotFoundException(ERROR_MESSAGE_AVAILABILITY_NOT_FOUND));

            // 2. Check for active bookings in the availability window
            if(!bookingDAO.anyActiveBookings(
                    user.getId(),
                    availability.getAvailableDate(),
                    availability.getStartTime(),
                    availability.getEndTime()))
            {
                availabilityDAO.delete(availability);
                return ResponseEntity.ok().build();
            }
            throw new AvailabilityBookingConflictException(
                    ERROR_MESSAGE_DELETE_AVAILABILITY_BOOKING_CONFLICT
            );
        }
        throw new AccessDeniedException(ERROR_MESSAGE_ACCESS_DENIED);
    }



    // Helper methods
    private User getUserContext(final Long userID,
                                final Authentication authentication)
    {
        if(authentication != null) {
            return (User) authentication.getPrincipal();
        } else {
            return userDAO.getUserById(userID).orElseThrow(
                    () -> new UserNotFoundException(ERROR_MESSAGE_GENERAL_USER_NOT_FOUND)
            );
        }
    }

    private void validateOverlapAndUpdateAvailability(final User user,
                                                      final AvailabilityRequestDTO request,
                                                      Availability availability)
    {
        if(!availabilityDAO.isAvailabilityOverlapping(
                availability.getId(),
                user.getId(),
                request.availableDate(),
                request.startTime(),
                request.endTime()))
        {
            availability.setAvailableDate(request.availableDate());
            availability.setStartTime(request.startTime());
            availability.setEndTime(request.endTime());
        }
        throw new AvailabilityOverlapException(ERROR_MESSAGE_UPDATE_AVAILABILITY_OVERLAP);
    }

    private Availability validateOverlapAndPersistAvailability(final User user,
                                                               final AvailabilityRequestDTO request)
    {
        if(!availabilityDAO.isAvailabilityOverlapping(
                null,
                user.getId(),
                request.availableDate(),
                request.startTime(),
                request.endTime()))
        {
            Availability availability = new Availability();
            AvailabilityUtil.setAvailabilityInfo(user, request, availability);
            availabilityDAO.create(availability);
            return availability;
        }
        throw new AvailabilityOverlapException("CREATE AVAILABILITY ERROR: " + ERROR_MESSAGE_AVAILABILITY_OVERLAP);
    }

    private List<AvailabilityResponseDTO> generatePastOrCurrentAvailabilities(final User user,
                                                                              final String time)
    {
        ZoneId userZone = ZoneId.of(user.getTimezone());
        switch(time)
        {
            case PAST -> {
                List<Availability> results = availabilityDAO.getPastAvailabilities(
                        user.getId(),
                        TimezoneUtil.getCurrentDate(userZone, clock),  // today
                        TimezoneUtil.getCurrentTime(userZone, clock)   // now
                );
                return AvailabilityUtil.generateResponse(results);
            }
            case CURRENT -> {
                List<Availability> results = availabilityDAO.getUpcomingAvailabilities(
                        user.getId(),
                        TimezoneUtil.getCurrentDate(userZone, clock),  // today
                        TimezoneUtil.getCurrentTime(userZone, clock)   // now
                );
                return AvailabilityUtil.generateResponse(results);
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    private void validateQuery(final String timezone,
                               final LocalDate date,
                               final LocalTime startTime,
                               final LocalTime endTime,
                               final Clock clock,
                               final int page,
                               final int size)
    {
        if (date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Date, start time, and end time are required."
            );
        }

        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative.");
        }

        if (size < QUERY_PAGE_SIZE_MIN || size > QUERY_PAGE_SIZE_MAX) {
            throw new IllegalArgumentException(
                    "Page size must be between "
                            + QUERY_PAGE_SIZE_MIN + " and "
                            + QUERY_PAGE_SIZE_MAX + "."
            );
        }

        // Validate date is not in the past
        validateAvailableDateNotInPast(
                timezone,
                date,
                clock,
                new InvalidAvailabilityDateException(ERROR_MESSAGE_QUERY_DATE_IN_PAST)
        );

        // Validate start time if searching for today
        validateStartTimeInFutureIfToday(
                timezone,
                date,
                startTime,
                clock,
                new InvalidAvailabilityTimeException(ERROR_MESSAGE_START_TIME_TODAY_NOT_IN_FUTURE)
        );

        // Validate start time is before end time
        validateStartBeforeEnd(
                startTime,
                endTime,
                new InvalidAvailabilityTimeException(ERROR_MESSAGE_QUERY_START_AFTER_END)
        );
    }
}
