package com.johnny.petscheduler.services;

import com.johnny.petscheduler.DAO.AvailabilityDAO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.PetBusiness;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.availability.AvailabilityOverlapException;
import com.johnny.petscheduler.utils.AvailabilityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_ACCESS_DENIED;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_AVAILABILITY_OVERLAP;

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
    public AvailabilityResponseDTO getAvailability(final Long availabilityID)
    {
        return availabilityDAO.getAvailabilityById(availabilityID)
                .orElse(throw new AvailabilityNotFound);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getOwnAvailabilities(final Authentication authentication)
    {
        // Check if the availability belongs to the user

        // Get all upcoming availabilities from DB
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getUserAvailabilities(final Long userID)
    {
        // Get all upcoming availabilities from DB
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
            AvailabilityUtil.validateAvailability(request);
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
                AvailabilityUtil.validateAvailability(request);
                Availability availability = validateOverlapAndPersistAvailability(user, request);
                availabilities.add(AvailabilityUtil.generateResponse(availability));
            }
            return availabilities;
        }
        throw new AccessDeniedException(ERROR_MESSAGE_ACCESS_DENIED);
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
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    public ResponseEntity<Void> deleteAvailabilitiesInBulk(final List<Long> requests,
                                                           final Authentication authentication)
    {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Availability validateOverlapAndPersistAvailability(final User user,
                                                               final AvailabilityRequestDTO request)
    {
        if(!availabilityDAO.isAvailabilityOverlapping(user.getId(), request.availableDate(),
                                                      request.startTime(), request.endTime()))
        {
            Availability availability = new Availability();
            AvailabilityUtil.setAvailabilityInfo(user, request, availability);
            availabilityDAO.create(availability);
            return availability;
        }
        throw new AvailabilityOverlapException(ERROR_MESSAGE_AVAILABILITY_OVERLAP);
    }
}
