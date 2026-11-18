package com.johnny.petscheduler.DAO;

import java.time.LocalDate;
import java.time.LocalTime;

public interface BookingDAO
{
    // Create

    // Read

    // Update

    // Delete

    // Other Queries
    boolean anyActiveBookings(final Long userID,
                              LocalDate date,
                              LocalTime startTime,
                              LocalTime endTime);
}
