package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.services.AvailabilityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_EMPTY_AVAILABILITY;
import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_MAX_BULK_AVAILABILITY_CREATE_LIMIT;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController
{
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // Create (POST)
    @PostMapping
    public ResponseEntity<AvailabilityResponseDTO> createAvailability(@Valid @RequestBody
                                                                      final AvailabilityRequestDTO request,
                                                                      final Authentication authentication)
    {
        AvailabilityResponseDTO response = availabilityService.createAvailability(request, authentication);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AvailabilityResponseDTO>> createBulkAvailabilities(
            @RequestBody
            @NotEmpty(message = ERROR_MESSAGE_EMPTY_AVAILABILITY)
            @Size(max = 5, message = ERROR_MESSAGE_MAX_BULK_AVAILABILITY_CREATE_LIMIT)
            final List<@Valid AvailabilityRequestDTO> requests,
            final Authentication authentication)
    {
        List<AvailabilityResponseDTO> responses = availabilityService
                .createBulkAvailabilities(requests, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responses);
    }

    // Read (GET)
    @GetMapping("/{availabilityID}")
    public ResponseEntity<AvailabilityResponseDTO> getAvailability(@PathVariable @Positive
                                                                   final Long availabilityID)
    {
        AvailabilityResponseDTO response = availabilityService.getAvailability(availabilityID);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AvailabilityResponseDTO>> getOwnAvailabilities(final Authentication authentication)
    {
         List<AvailabilityResponseDTO> response = availabilityService.getAvailabilities(
                 null, authentication
         );
         return ResponseEntity
                 .status(HttpStatus.OK)
                 .body(response);
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<List<AvailabilityResponseDTO>> getUserAvailabilities(@PathVariable @Positive
                                                                               final Long userID)
    {
        List<AvailabilityResponseDTO> response = availabilityService.getAvailabilities(
                userID, null
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves a list of own past availabilities.
     *
     * @param authentication
     *      An Authenticated User object
     *
     * @return <code>List of Own Past Availabilities</code>
     */
    @GetMapping("/me/history")
    public ResponseEntity<List<AvailabilityResponseDTO>> getPreviousOwnAvailabilities(final Authentication authentication)
    {
        List<AvailabilityResponseDTO> response = availabilityService.getPreviousAvailabilities(
                null, authentication
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves a list of a provider's (PetSitter/PetBusiness) past availabilities.
     *
     * @param userID
     *      A Long for the user's ID
     *
     * @return <code>List of the Provider's Past Availabilities</code>
     */
    @GetMapping("/user/{userID}/history")
    public ResponseEntity<List<AvailabilityResponseDTO>> getPreviousUserAvailabilities(@PathVariable @Positive
                                                                                       final Long userID)
    {
        List<AvailabilityResponseDTO> response = availabilityService
                .getPreviousAvailabilities(
                        userID, null
                );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Queries and returns a List of Availabilities based on the user's
     * timezone and search preference.
     *
     * @implNote <code>API Example</code>
     *      GET /api/availabilities/search?date=2026-11-10&startTime=09:00&endTime=12:00&page=0&size=20
     *
     * @param date
     *      A LocalDate object
     *
     * @param startTime
     *      A LocalTime object
     *
     * @param endTime
     *      A LocalTime object
     *
     * @param authentication
     *      An authenticated user object
     *
     * @return <code>List of AvailabilityResponseDTO</code>
     */
    @GetMapping("/search")
    public ResponseEntity<List<AvailabilityResponseDTO>> searchAvailabilitiesByQuery(
            @RequestParam final LocalDate date,
            @RequestParam final LocalTime startTime,
            @RequestParam final LocalTime endTime,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            final Authentication authentication)
    {
        List<AvailabilityResponseDTO> availabilities = availabilityService
                .getAvailabilitiesByQuery(
                        date, startTime, endTime,
                        page, size, authentication
                );

        return ResponseEntity.ok(availabilities);
    }

    // Update (PUT)
    @PutMapping("/{availabilityID}")
    public ResponseEntity<AvailabilityResponseDTO> updateAvailability(@PathVariable @Positive
                                                                      final Long availabilityID,
                                                                      @Valid @RequestBody
                                                                      final AvailabilityRequestDTO request,
                                                                      final Authentication authentication)
    {
        AvailabilityResponseDTO response = availabilityService.updateAvailability(
                availabilityID, request, authentication
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Delete (DELETE)
    @DeleteMapping("/{availabilityID}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable @Positive
                                                   final Long availabilityID,
                                                   final Authentication authentication)
    {
        return availabilityService.deleteAvailability(availabilityID, authentication);
    }
}
