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
    Optional<Availability> getAvailabilityByIdAndProviderId(final Long availabilityID, final Long userID);
    List<Availability> getAllAvailabilitiesByUserID(final long userID);
    List<Availability> getUpcomingAvailabilities(final Long userID, final LocalDate today, final LocalTime now);
    List<Availability> getPastAvailabilities(final Long userID, final LocalDate today, final LocalTime now);
    List<Availability> getAvailabilitiesByDateAndTime(final LocalDate requestedDate,
                                                      final LocalTime requestedStart,
                                                      final LocalTime requestedEnd,
                                                      final int page, final int size);
    List<Availability> getAvailabilitiesByDateAndTime(final Long userID,
                                                      final LocalDate requestedDate,
                                                      final LocalTime requestedStart,
                                                      final LocalTime requestedEnd,
                                                      final int page, final int size);

    // Update (PUT)
    Availability update(final Availability availability);

    // Delete
    void delete(final Availability availability);

    // Validation
    boolean isAvailabilityOverlapping(final Long availabilityID,
                                      final Long userID,
                                      final LocalDate date,
                                      final LocalTime startTime,
                                      final LocalTime endTime);
}
