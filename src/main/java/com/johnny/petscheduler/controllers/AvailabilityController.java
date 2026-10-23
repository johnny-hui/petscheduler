package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.DTO.availabilities.AvailabilityResponseDTO;
import com.johnny.petscheduler.services.AvailabilityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_EMPTY_AVAILABILITY;

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
        AvailabilityResponseDTO response = availabilityService
                .createAvailability(request, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AvailabilityResponseDTO>> createAvailabilities(
            @Valid @RequestBody @NotEmpty(message = ERROR_MESSAGE_EMPTY_AVAILABILITY)
            List<@Valid AvailabilityRequestDTO> requests,
            final Authentication authentication)
    {
        List<AvailabilityResponseDTO> responses = availabilityService
                .createAvailabilities(requests, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responses);
    }

    // Read (GET)
    @GetMapping("/{availabilityID}")
    public ResponseEntity<AvailabilityResponseDTO> getAvailability(@PathVariable
                                                                   final Long availabilityID,
                                                                   final Authentication authentication)
    {
        AvailabilityResponseDTO response = availabilityService
                .getAvailability(availabilityID, authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AvailabilityResponseDTO>> getOwnAvailabilities(final Authentication authentication)
    {
         List<AvailabilityResponseDTO> response = availabilityService
                 .getOwnAvailabilities(authentication);

         return ResponseEntity
                 .status(HttpStatus.OK)
                 .body(response);
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<List<AvailabilityResponseDTO>> getUserAvailabilities(@PathVariable
                                                                               final Long userID,
                                                                               final Authentication authentication)
    {
        List<AvailabilityResponseDTO> response = availabilityService
                .getUserAvailabilities(userID, authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Update (PUT)
    @PutMapping
    public ResponseEntity<AvailabilityResponseDTO> updateAvailability(@Valid @RequestBody
                                                                      final AvailabilityRequestDTO request,
                                                                      final Authentication authentication)
    {
        AvailabilityResponseDTO response = availabilityService
                .updateAvailability(request, authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // Delete (DELETE)
    // NOTE: Should only be able to delete any availabilities from the current date
    // NOTE: Should only be able to delete ^^ if there are no active and REQUESTED bookings within window
    // NOTE: Otherwise, user has to deal with any conflicts first
    @DeleteMapping("/{availabilityID}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable final Long availabilityID,
                                                   final Authentication authentication)
    {
        return availabilityService.deleteAvailability(availabilityID, authentication);
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteAvailabilities(
            @Valid @RequestBody @NotEmpty(message = ERROR_MESSAGE_EMPTY_AVAILABILITY)
            final List<@NotNull Long> availabilityIDList,
            final Authentication authentication)
    {
        return availabilityService.deleteAvailabilitiesInBulk(availabilityIDList, authentication);
    }
}
