package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.Availability;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AvailabilityDAO
{
    // Create (POST)
    Availability create(Availability availability);

    // Read (GET)
    Optional<Availability> getAvailabilityById(final long availabilityId);
    List<Availability> getAllAvailabilitiesByUserID(final long userID);
    List<Availability> getUpcomingAvailabilities(final Long userID,
                                                 final LocalDate today,
                                                 final LocalTime now);
    List<Availability> getPastAvailabilities(final Long userID,
                                             final LocalDate today,
                                             final LocalTime now);

    // Update (PUT)
    Availability update(final Availability availability);

    // Validation
    boolean isAvailabilityOverlapping(final Long sitterId,
                                      final LocalDate date,
                                      final LocalTime startTime,
                                      final LocalTime endTime);
}
