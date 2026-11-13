package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.Availability;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AvailabilityDAOImpl implements AvailabilityDAO
{
    private final EntityManager em;

    @Autowired
    public AvailabilityDAOImpl(final EntityManager em) {
        this.em = em;
    }

    /**
     * Creates and persists an Availability object
     *
     * @param availability
     *      An Availability object
     *
     * @return <code>Availability</code>
     *      The persisted object from DB
     */
    @Override
    public Availability create(Availability availability) {
        em.persist(availability);
        return availability;
    }

    /**
     * Returns a specific Availability if it exists.
     *
     * @param availabilityID
     *      A Long for the ID of the specific availability
     *
     * @return <code>Optional</code>
     *      An Optional object possibly containing the Availability
     */
    @Override
    public Optional<Availability> getAvailabilityById(final long availabilityID)
    {
        TypedQuery<Availability> query = em.createQuery(
                """
                SELECT a
                FROM Availability a
                WHERE a.id = :availabilityID
                """,
                Availability.class
        );

        query.setParameter("availabilityID", availabilityID);
        return query.getResultStream().findFirst();
    }


    /**
     * Optionally returns an Availability based on its ID and
     * the user's ID.
     *
     * @param availabilityID
     *      A Long for the specific availability's ID
     *
     * @param userID
     *      A Long for the user's ID
     *
     * @return <code>Optional</code>
     */
    @Override
    public Optional<Availability> getAvailabilityByIdAndProviderId(final Long availabilityID,
                                                                   final Long userID)
    {
        TypedQuery<Availability> query = em.createQuery(
        """
        SELECT a
        FROM Availability a
        WHERE a.id = :availabilityId
        AND a.serviceProvider.id = :userID
        """, Availability.class);

        query.setParameter("availabilityId", availabilityID);
        query.setParameter("userID", userID);
        return query.getResultStream().findFirst();
    }

    /**
     * Returns all availabilities for a specific user based on userID.
     *
     * @param userID
     *      A Long for the user ID
     *
     * @return <code>List of Availabilities</code>
     */
    @Override
    public List<Availability> getAllAvailabilitiesByUserID(final long userID)
    {
        TypedQuery<Availability> query = em.createQuery(
                """
                SELECT a
                FROM Availability a
                WHERE a.serviceProvider.id = :userID
                ORDER BY a.id ASC
                """,
                Availability.class
        );
        query.setParameter("userID", userID);
        return query.getResultList();
    }

    /**
     * Updates an Availability object.
     *
     * @param availability
     *      An Availability object
     *
     * @return <code>Availability</code>
     *      The updated object
     */
    @Override
    public Availability update(final Availability availability) {
        return em.merge(availability);
    }

    /**
     * Gets a list of current and upcoming Availabilities from the database
     * in a sorted manner from date and start time.
     *
     * @param userID
     *      A Long for the User ID
     *
     * @param today
     *      A LocalDate object for current day
     *
     * @param now
     *      A LocalTime object for the current time
     *
     * @return <code>List of Availabilities</code>
     */
    public List<Availability> getUpcomingAvailabilities(final Long userID,
                                                        final LocalDate today,
                                                        final LocalTime now)
    {
        TypedQuery<Availability> query = em.createQuery(
                """
                SELECT a
                FROM Availability a
                WHERE a.serviceProvider.id = : userID
                  AND (
                      a.availableDate > :today
                      OR (
                          a.availableDate = :today
                          AND a.endTime > :now
                      )
                  )
                ORDER BY a.availableDate ASC, a.startTime ASC
                """,
                Availability.class
        );

        query.setParameter("userID", userID);
        query.setParameter("today", today);
        query.setParameter("now", now);
        return query.getResultList();
    }

    /**
     * Gets a list of past Availabilities from the database.
     *
     * @param userID
     *      A Long for the User ID
     *
     * @param today
     *      A LocalDate object for current day
     *
     * @param now
     *      A LocalTime object for the current time
     *
     * @return <code>List of Availabilities</code>
     */
    public List<Availability> getPastAvailabilities(final Long userID,
                                                    final LocalDate today,
                                                    final LocalTime now)
    {
        TypedQuery<Availability> query = em.createQuery(
                """
                SELECT a
                FROM Availability a
                WHERE a.serviceProvider.id = : userID
                  AND (
                      a.availableDate < :today
                      OR (
                          a.availableDate = :today
                          AND a.endTime <= :now
                      )
                  )
                ORDER BY a.availableDate DESC, a.startTime DESC
                """,
                Availability.class
        );

        query.setParameter("userID", userID);
        query.setParameter("today", today);
        query.setParameter("now", now);
        return query.getResultList();
    }

    /**
     * An over-loaded method that returns a list of Availabilities based on that satisfies
     * the requested date, start and end times (without user ID).
     *
     * @param requestedDate
     *      A LocalDate object
     *
     * @param requestedStart
     *      A LocalTime object
     *
     * @param requestedEnd
     *      A LocalTime object
     *
     * @param page
     *      An integer for the page number
     *
     * @param size
     *      An integer for the page size
     *
     * @return <code>A list of Availabilities</code>
     */
    @Override
    public List<Availability> getAvailabilitiesByDateAndTime(final LocalDate requestedDate,
                                                             final LocalTime requestedStart,
                                                             final LocalTime requestedEnd,
                                                             final int page,
                                                             final int size)
    {
        TypedQuery<Availability> query = em.createQuery(
                """
                SELECT a
                FROM Availability a
                WHERE a.availableDate = :requestedDate
                  AND a.startTime <= :requestedStart
                  AND a.endTime >= :requestedEnd
                ORDER BY a.startTime ASC
                """,
                Availability.class
        );

        query.setParameter("requestedDate", requestedDate);
        query.setParameter("requestedStart", requestedStart);
        query.setParameter("requestedEnd", requestedEnd);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    /**
     * Returns a List of Availabilities by a provider (user ID) that satisfies
     * the requested date, start and end times.
     *
     * @param userID
     *      A Long for the userID
     *
     * @param requestedDate
     *      A LocalDate object
     *
     * @param requestedStart
     *      A LocalTime object
     *
     * @param requestedEnd
     *      A LocalTime object
     *
     * @param page
     *      An integer for the page number
     *
     * @param size
     *      An integer for the page size
     *
     * @return <code>A list of Availabilities</code>
     */
    @Override
    public List<Availability> getAvailabilitiesByDateAndTime(final Long userID,
                                                             final LocalDate requestedDate,
                                                             final LocalTime requestedStart,
                                                             final LocalTime requestedEnd,
                                                             final int page,
                                                             final int size)
    {
        TypedQuery<Availability> query = em.createQuery(
               """
               SELECT a
               FROM Availability a
               WHERE a.serviceProvider.id = :userID
                 AND a.availableDate = :requestedDate
                 AND a.startTime <= :requestedStart
                 AND a.endTime >= :requestedEnd
               ORDER BY a.startTime ASC
               """,
                Availability.class
        );

        query.setParameter("userID", userID);
        query.setParameter("requestedDate", requestedDate);
        query.setParameter("requestedStart", requestedStart);
        query.setParameter("requestedEnd", requestedEnd);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    /**
     * Deletes an Availability from the DB.
     *
     * @param availability
     *      An Availability object
     */
    @Override
    public void delete(final Availability availability)
    {
        em.remove(availability);
    }

    /**
     * Checks whether the requested Availability overlaps with any existing
     * Availability in the database.
     *
     * @param userID
     *      A Long for the user ID
     *
     * @param availabilityID
     *      A Long for a specific Availability's ID
     *
     * @param requestedDate
     *      A LocalDate object for the requested date
     *
     * @param requestedStartTime
     *      A LocalTime object for the requested start time
     *
     * @param requestedEndTime
     *      A LocalTime object for the requested end time
     *
     * @return <code>bool(T/F)</code>
     *      True if overlapping, False otherwise
     */
    @Override
    public boolean isAvailabilityOverlapping(final Long availabilityID,
                                             final Long userID,
                                             final LocalDate requestedDate,
                                             final LocalTime requestedStartTime,
                                             final LocalTime requestedEndTime)
    {
        TypedQuery<Long> query = em.createQuery("""
            SELECT a.id
            FROM Availability a
            WHERE a.serviceProvider.id = :userID
              AND a.availableDate = :date
              AND a.startTime < :endTime
              AND a.endTime > :startTime
              AND (:availabilityID IS NULL
                   OR a.id <> :availabilityID)
            """, Long.class);

        query.setParameter("availabilityID", availabilityID);
        query.setParameter("userID", userID);
        query.setParameter("date", requestedDate);
        query.setParameter("startTime", requestedStartTime);
        query.setParameter("endTime", requestedEndTime);

        query.setMaxResults(1);
        return query.getResultList().isEmpty() ? false : true;
    }
}
