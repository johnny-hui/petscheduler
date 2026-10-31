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
     * Checks whether the requested Availability overlaps with any existing
     * Availability in the database.
     *
     * @param userID
     *      A Long for the user ID
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
    public boolean isAvailabilityOverlapping(final Long userID,
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
        """, Long.class);

        query.setParameter("userID", userID);
        query.setParameter("date", requestedDate);
        query.setParameter("startTime", requestedStartTime);
        query.setParameter("endTime", requestedEndTime);

        query.setMaxResults(1);
        return query.getResultList().isEmpty() ? false : true;
    }

    /**
     * Gets a list of current and upcoming Availabilities from the database.
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
}
