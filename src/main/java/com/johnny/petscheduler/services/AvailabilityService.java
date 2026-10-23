package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.AvailabilityDAO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.johnny.petscheduler.utils.AvailabilityUtil.isAvailableDateInPast;

@Service
public class AvailabilityService
{
    private final AvailabilityDAO availabilityDAO;

    @Autowired
    public AvailabilityService(final AvailabilityDAO availabilityDAO) {
        this.availabilityDAO = availabilityDAO;
    }

    // Read (GET)
    @Transactional(readOnly = true)
    public AvailabilityResponseDTO getAvailability(final Long id,
                                                   final Authentication authentication)
    {
        // Check if the availability belongs to the user

    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getOwnAvailabilities(final Authentication authentication)
    {
        // Check if the availability belongs to the user
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getUserAvailabilities(final Long userID,
                                                               final Authentication authentication)
    {

    }

    // Create (POST)

    @Transactional
    public AvailabilityResponseDTO createAvailability(final AvailabilityRequestDTO availabilityRequestDTO,
                                                      final Authentication authentication)
    {
        // Get User object after passing Spring Authentication
        User user = (User) authentication.getPrincipal();

        // Perform user role check
        if(user instanceof PetSitter || user instanceof PetBusiness) {
            if(!isAvailableDateInPast(availabilityRequestDTO)) {
                // Check: The date and times are present.

                // Check: startTime is before endTime.

                // Check: The date isn't in the past.

                // Check: If it's today, the start time must be past current time.

                // Check: If start and end times

                // Check: Minimum 3 hour gap required between startTime and endTime

                // Check: For any overlapping availability windows (might require queries)
            }
        }
        throw new AccessDeniedException(ERROR_MESSAGE_ACCESS_DENIED);
    }

    @Transactional
    public List<AvailabilityResponseDTO> createAvailabilities(final List<AvailabilityRequestDTO> requests,
                                                              final Authentication authentication)
    {
        return null;
    }


    // Update (PUT)
    @Transactional
    public AvailabilityResponseDTO updateAvailability(final AvailabilityRequestDTO availabilityRequestDTO,
                                                      final Authentication authentication)
    {
        return null;
    }

    // Delete (DELETE)
    @Transactional
    public ResponseEntity<Void> deleteAvailability(final Long id,
                                                   final Authentication authentication)
    {
        return null;
    }

    @Transactional
    public ResponseEntity<Void> deleteAvailabilitiesInBulk(final List<Long> availabilityIDList,
                                                           final Authentication authentication)
    {
        return null;
    }
}
