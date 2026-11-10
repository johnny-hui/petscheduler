package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.booking.BookingStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class BookingDAOImpl implements BookingDAO
{
    private final EntityManager em;

    @Autowired
    public BookingDAOImpl(final EntityManager em) {
        this.em = em;
    }

    /**
     * Checks for any active Bookings within the provided date, start
     * and end times.
     *
     * @param userID
     *      A Long for the user ID
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
     * @return <code>bool(T/F)</code>
     *      True if active Booking(s) are present, False otherwise
     */
    @Override
    public boolean anyActiveBookings(final Long userID,
                                     final LocalDate date,
                                     final LocalTime startTime,
                                     final LocalTime endTime)
    {
        TypedQuery<Long> query = em.createQuery(
            """
            SELECT COUNT(b)
            FROM Booking b
            WHERE b.serviceProvider.id = :userID
              AND b.bookingDate = :date
              AND b.startTime < :endTime
              AND b.endTime > :startTime
              AND b.status IN :statuses
            """, Long.class
        );

        query.setParameter("userID", userID);
        query.setParameter("date", date);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        query.setParameter("statuses", List.of(
                BookingStatus.REQUESTED,
                BookingStatus.CONFIRMED
        ));

        query.setMaxResults(1);
        return query.getResultList().isEmpty() ? false : true;
    }
}
