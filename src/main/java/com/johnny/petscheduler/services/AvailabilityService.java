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
import static com.johnny.petscheduler.utils.AvailabilityUtil.validateAvailableDate;

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

        // Get the specific availability from DB
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getOwnAvailabilities(final Authentication authentication)
    {
        // Check if the availability belongs to the user

        // Get all availabilities from DB
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getUserAvailabilities(final Long userID,
                                                               final Authentication authentication)
    {
        // Check if the availability belongs to the user

        // Get all availabilities from DB
    }

    // Create (POST)
    @Transactional
    public AvailabilityResponseDTO createAvailability(final AvailabilityRequestDTO request,
                                                      final Authentication authentication)
    {
        // Get User object after passing Spring Authentication
        User user = (User) authentication.getPrincipal();

        // Perform user role check and validate the Availability request
        if(user instanceof PetSitter || user instanceof PetBusiness) {
            if(!validateAvailableDate(request)) {
                // Constraint 1: If it's date is TODAY, the start time must be in the future

                // Constraint 2: startTime must be before endTime.

                // Constraint 3: Start and end times are of 15 minute increments

                // Constraint 4: Availability must be at least 3 hours minimum

                // Constraint 5: Availability must be within 90 days plus current date

                // Constraint 6: For any overlapping availability windows

                // Persist Availability
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
    public AvailabilityResponseDTO updateAvailability(final AvailabilityRequestDTO request,
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
    public ResponseEntity<Void> deleteAvailabilitiesInBulk(final List<Long> requests,
                                                           final Authentication authentication)
    {
        return null;
    }
}
