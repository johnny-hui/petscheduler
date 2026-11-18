package com.johnny.petscheduler.DTO.availabilities;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This record contains data for updating an Availability.
 *
 * @param availableDate
 *      A LocalDate object
 *
 * @param startTime
 *      A LocalTime object that represents the start time
 *
 * @param endTime
 *      A LocalTime object that represents the end time
 */
public record UpdateAvailabilityDTO(
        @NotNull(message = "An available date must be provided")
        LocalDate availableDate,

        @NotNull(message = "A start time for the date selected must be provided")
        LocalTime startTime,

        @NotNull(message = "An end time for the date selected must be provided")
        LocalTime endTime
){}
