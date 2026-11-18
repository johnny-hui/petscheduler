package com.johnny.petscheduler.DTO.availabilities;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This record contains data for an Availability from the server.
 *
 * @param id
 *      A Long for the ID of the availability
 *
 * @param availableDate
 *      A LocalDate object for the specific date of the availability
 *
 * @param startTime
 *      A LocalTime object that represents the start time
 *
 * @param endTime
 *      A LocalTime object that represents the end time
 */
public record AvailabilityResponseDTO(
        Long id,
        LocalDate availableDate,
        LocalTime startTime,
        LocalTime endTime
) {}