package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.security.user.UserRole;
import com.johnny.petscheduler.utils.TestInitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AvailabilityDAOImpl.class)
class AvailabilityDAOImplTest
{
    @Autowired
    private AvailabilityDAOImpl availabilityDAO;

    @Autowired
    private TestEntityManager testEntityManager;

    private User sitter;
    private User otherSitter;
    private static final LocalDate TEST_TODAY = LocalDate.of(2026, 11, 13);

    @BeforeEach
    void setUp() {
        sitter = TestInitUtil.createMockUser(UserRole.PET_SITTER, "john1@outlook.com");
        otherSitter = TestInitUtil.createMockUser(UserRole.PET_SITTER, "john2@outlook.com");

        testEntityManager.persistAndFlush(sitter);
        testEntityManager.persistAndFlush(otherSitter);
    }

    // Create Tests
    @Test
    void create_shouldPersistAvailabilitySuccessfully()
    {
        // Arrange
        Availability availability = new Availability();
        availability.setServiceProvider(sitter);
        availability.setAvailableDate(TEST_TODAY.plusDays(1));
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(12, 0));

        // Act
        Availability result = availabilityDAO.create(availability);
        flushAndClear();

        // Assert
        Availability persisted = testEntityManager.find(
                Availability.class,
                result.getId()
        );

        assertThat(result.getId()).isNotNull();
        assertThat(persisted).isNotNull();
        assertThat(persisted.getAvailableDate())
                .isEqualTo(TEST_TODAY.plusDays(1));
        assertThat(persisted.getStartTime())
                .isEqualTo(LocalTime.of(9, 0));
        assertThat(persisted.getEndTime())
                .isEqualTo(LocalTime.of(12, 0));
    }

    // Get Tests
    @Test
    void getAvailabilityById_shouldReturnAvailabilityWhenExists()
    {
        // Arrange
        Availability availability = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        Long availabilityID = availability.getId();
        flushAndClear();

        // Act
        Optional<Availability> result = availabilityDAO.getAvailabilityById(availabilityID);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(availabilityID);
    }

    /**
     * This test ensures existence and ownership for a specific Availability.
     */
    @Test
    void getAvailabilityByIdAndProviderId_shouldReturnMatchingAvailability() {
        Availability availability = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        flushAndClear();

        Optional<Availability> result =
                availabilityDAO.getAvailabilityByIdAndProviderId(
                        availability.getId(),
                        sitter.getId()
                );

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(availability.getId());
    }

    @Test
    void getAvailabilityByIdAndProviderId_shouldReturnEmptyForWrongProvider() {
        Availability availability = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        flushAndClear();

        Optional<Availability> result =
                availabilityDAO.getAvailabilityByIdAndProviderId(
                        availability.getId(),
                        otherSitter.getId()
                );

        assertThat(result).isEmpty();
    }

    @Test
    void getAvailabilityByIdAndProviderId_shouldReturnEmptyWhenNotFound() {
        Optional<Availability> result =
                availabilityDAO.getAvailabilityByIdAndProviderId(
                        Long.MAX_VALUE,
                        sitter.getId()
                );

        assertThat(result).isEmpty();
    }

    @Test
    void getAvailabilityById_shouldReturnEmptyWhenNotFound()
    {
        Optional<Availability> result = availabilityDAO.getAvailabilityById(Long.MAX_VALUE);
        assertThat(result).isEmpty();
    }

    @Test
    void getAllAvailabilitiesByUserID_shouldReturnOnlyProvidersAvailabilitiesInIdOrder() {
        // Arrange
        Availability first = persistAvailability(
                (PetSitter) sitter, TEST_TODAY.plusDays(1),
                LocalTime.of(9, 0), LocalTime.of(12, 0));

        persistAvailability(
                (PetSitter) otherSitter, TEST_TODAY.plusDays(1),
                LocalTime.of(13, 0), LocalTime.of(16, 0));

        Availability second = persistAvailability(
                (PetSitter) sitter, TEST_TODAY.plusDays(2),
                LocalTime.of(13, 0), LocalTime.of(16, 0));

        flushAndClear();

        // Act
        List<Availability> result = availabilityDAO.getAllAvailabilitiesByUserID(sitter.getId());

        // Assert
        assertThat(result)
                .extracting(Availability::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void getAllAvailabilitiesByUserID_shouldReturnEmptyWhenNoneExist()
    {
        List<Availability> result = availabilityDAO.getAllAvailabilitiesByUserID(sitter.getId());
        assertThat(result.isEmpty()).isEqualTo(true);
    }

    // Update Tests
    @Test
    void update_shouldPersistModifiedAvailability() {
        // Arrange
        Availability availability = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );
        Long id = availability.getId();
        flushAndClear();

        // Detached entity
        availability.setStartTime(LocalTime.of(10, 0));
        availability.setEndTime(LocalTime.of(14, 0));

        // Act
        Availability updated = availabilityDAO.update(availability);
        flushAndClear();

        // Assert
        Availability persisted = testEntityManager.find(Availability.class, id);
        assertThat(updated.getId())
                .isEqualTo(id);
        assertThat(persisted.getStartTime())
                .isEqualTo(LocalTime.of(10, 0));
        assertThat(persisted.getEndTime())
                .isEqualTo(LocalTime.of(14, 0));
    }

    @Test
    void delete_shouldRemoveAvailabilityFromDatabase() {
        // Arrange
        Availability availability = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );
        Long id = availability.getId();

        // Act
        availabilityDAO.delete(availability);
        flushAndClear();

        // Assert
        Availability persisted = testEntityManager.find(Availability.class, id);
        Optional<Availability> result = availabilityDAO.getAvailabilityById(id);
        assertThat(persisted).isNull();
        assertThat(result).isEmpty();
    }

    // Getting Availabilities (Current + Future)
    @Test
    void getUpcomingAvailabilities_shouldReturnCurrentAndFutureInAscendingOrder() {
        // Arrange
        LocalTime now = LocalTime.of(12, 0);

        Availability todayOngoing = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY,
                LocalTime.of(10, 0),
                LocalTime.of(13, 0)
        );

        Availability tomorrowMorning = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        Availability tomorrowAfternoon = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(14, 0),
                LocalTime.of(17, 0)
        );

        persistAvailability( // => Already ended
                (PetSitter) sitter,
                TEST_TODAY,
                LocalTime.of(8, 0),
                LocalTime.of(11, 0)
        );

        persistAvailability( // => Another provider (sitter)
                (PetSitter) otherSitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(11, 0)
        );
        flushAndClear();

        // Act
        List<Availability> result = availabilityDAO.getUpcomingAvailabilities(
                sitter.getId(),
                TEST_TODAY, // 2026-11-13
                now  // 12 PM
        );

        // Assert
        assertThat(result)
                .extracting(Availability::getId)
                .containsExactly(
                        todayOngoing.getId(),
                        tomorrowMorning.getId(),
                        tomorrowAfternoon.getId()
                );
        assertThat(result).size().isEqualTo(3);
    }

    @Test
    void getUpcomingAvailabilities_shouldExcludeAvailabilityEndingExactlyNow()
    {
        // Arrange
        persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        flushAndClear();

        // Act
        List<Availability> result =
                availabilityDAO.getUpcomingAvailabilities(
                        sitter.getId(),
                        TEST_TODAY,
                        LocalTime.NOON
                );

        // Assert
        assertThat(result).isEmpty();
    }

    // Getting Availabilities (Past)
    @Test
    void getPastAvailabilities_shouldReturnPastAvailabilitiesInDescendingOrder() {
        // Arrange
        LocalTime now = LocalTime.of(12, 0);

        Availability yesterdayMorning = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.minusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        Availability yesterdayAfternoon = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.minusDays(1),
                LocalTime.of(14, 0),
                LocalTime.of(17, 0)
        );

        Availability todayEnded = persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY,
                LocalTime.of(8, 0),
                LocalTime.of(11, 0)
        );

        // Ongoing, not past
        persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY,
                LocalTime.of(11, 0),
                LocalTime.of(14, 0)
        );

        // Another provider
        persistAvailability(
                (PetSitter) otherSitter,
                TEST_TODAY.minusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        flushAndClear();

        // Act
        List<Availability> result = availabilityDAO.getPastAvailabilities(
                sitter.getId(),
                TEST_TODAY, // 2026-11-13
                now // 12 PM
        );

        // Assert
        assertThat(result)
                .extracting(Availability::getId)
                .containsExactly(
                        todayEnded.getId(),
                        yesterdayAfternoon.getId(),
                        yesterdayMorning.getId()
                );
        assertThat(result).size().isEqualTo(3);
    }

    @Test
    void getPastAvailabilities_shouldIncludeAvailabilityEndingExactlyNow() {
        Availability availability = persistAvailability(
                (PetSitter) sitter, TEST_TODAY,
                LocalTime.of(9, 0), LocalTime.of(12, 0));

        flushAndClear();

        List<Availability> result =
                availabilityDAO.getPastAvailabilities(
                        sitter.getId(), TEST_TODAY, LocalTime.NOON);

        assertThat(result)
                .extracting(Availability::getId)
                .containsExactly(availability.getId());
    }

    // Getting Availabilities (Filter Query)
    @Test
    void getAvailabilitiesByDateAndTime_shouldReturnOnlyMatchingWindows() {
        // Arrange
        LocalDate date = TEST_TODAY.plusDays(1);

        Availability matching1 = persistAvailability(
                (PetSitter) sitter, date,
                LocalTime.of(9, 0), LocalTime.of(13, 0));

        Availability matching2 = persistAvailability(
                (PetSitter) otherSitter, date,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        // Starts too late
        persistAvailability(
                (PetSitter) sitter, date,
                LocalTime.of(11, 0), LocalTime.of(14, 0));

        // Ends too early
        persistAvailability(
                (PetSitter) sitter, date,
                LocalTime.of(8, 0), LocalTime.of(11, 0));

        // Wrong date
        persistAvailability(
                (PetSitter) sitter, date.plusDays(1),
                LocalTime.of(9, 0), LocalTime.of(13, 0));

        flushAndClear();

        // Act
        List<Availability> result =
                availabilityDAO.getAvailabilitiesByDateAndTime(
                        date,
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0),
                        0, 10
                );

        // Assert
        assertThat(result)
                .extracting(Availability::getId)
                .containsExactly(
                        matching1.getId(),
                        matching2.getId()
                );
    }

    @Test
    void getAvailabilitiesByDateAndTime_shouldApplyPagination() {
        // Arrange
        LocalDate date = TEST_TODAY.plusDays(1);

        Availability first = persistAvailability(
                (PetSitter) sitter, date,
                LocalTime.of(8, 0), LocalTime.of(14, 0));

        Availability second = persistAvailability(
                (PetSitter) sitter, date,
                LocalTime.of(9, 0), LocalTime.of(14, 0));

        Availability third = persistAvailability(
                (PetSitter) sitter, date,
                LocalTime.of(10, 0), LocalTime.of(14, 0));

        flushAndClear();

        // Act
        List<Availability> firstPage =
                availabilityDAO.getAvailabilitiesByDateAndTime(
                        date, LocalTime.of(11, 0),
                        LocalTime.of(12, 0), 0, 2);

        List<Availability> secondPage =
                availabilityDAO.getAvailabilitiesByDateAndTime(
                        date, LocalTime.of(11, 0),
                        LocalTime.of(12, 0), 1, 2);

        // Assert
        assertThat(firstPage)
                .extracting(Availability::getId)
                .containsExactly(first.getId(), second.getId());

        assertThat(secondPage)
                .extracting(Availability::getId)
                .containsExactly(third.getId());
    }

    @Test
    void getAvailabilitiesByDateAndTime_withProviderId_shouldFilterByProvider() {
        // Arrange
        LocalDate date = TEST_TODAY.plusDays(1);

        Availability matching = persistAvailability(
                (PetSitter) sitter, date,
                LocalTime.of(9, 0), LocalTime.of(13, 0));

        persistAvailability(
                (PetSitter) otherSitter, date,
                LocalTime.of(9, 0), LocalTime.of(13, 0));

        flushAndClear();

        // Act
        List<Availability> result =
                availabilityDAO.getAvailabilitiesByDateAndTime(
                        sitter.getId(),
                        date,
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0),
                        0, 10
                );

        // Assert
        assertThat(result)
                .extracting(Availability::getId)
                .containsExactly(matching.getId());
    }

    // Availability Overlap Test
    @ParameterizedTest
    @CsvSource({
            "10:00, 11:00, true",
            "08:00, 10:00, true",
            "11:00, 14:00, true",
            "08:00, 14:00, true",
            "09:00, 12:00, true",
            "12:00, 15:00, false",
            "06:00, 09:00, false"
    })
    void isAvailabilityOverlapping_shouldDetectOverlappingWindows(
            final LocalTime requestedStart,
            final LocalTime requestedEnd,
            boolean expected)
    {
        // Arrange
        LocalDate date = TEST_TODAY.plusDays(1);
        persistAvailability(
                (PetSitter) sitter,
                date,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        flushAndClear();

        // Act
        boolean result = availabilityDAO.isAvailabilityOverlapping(
                null,
                sitter.getId(),
                date,
                requestedStart,
                requestedEnd
        );

        // Assert
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void isAvailabilityOverlapping_shouldReturnFalseForDifferentProvider()
    {
        LocalDate date = TEST_TODAY.plusDays(1);

        persistAvailability(
                (PetSitter) otherSitter,
                date,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );
        flushAndClear();

        boolean result = availabilityDAO.isAvailabilityOverlapping(
                null,
                sitter.getId(),
                date,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        assertThat(result).isFalse();
    }

    @Test
    void isAvailabilityOverlapping_shouldReturnFalseForDifferentDate()
    {
        persistAvailability(
                (PetSitter) sitter,
                TEST_TODAY.plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        flushAndClear();

        boolean result = availabilityDAO.isAvailabilityOverlapping(
                null,
                sitter.getId(),
                TEST_TODAY.plusDays(2),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        assertThat(result).isFalse();
    }

    @Test
    void isAvailabilityOverlapping_whenUpdatingAvailability_shouldExcludeCurrentAvailability()
    {
        // Arrange
        LocalDate date = TEST_TODAY.plusDays(1);

        Availability availability = persistAvailability(
                (PetSitter) sitter,
                date,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        flushAndClear();

        // Act
        boolean result = availabilityDAO.isAvailabilityOverlapping(
                availability.getId(),
                sitter.getId(),
                date,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void isAvailabilityOverlapping_shouldDetectOtherAvailabilityWhenExcludingSelf() {
        // Arrange
        LocalDate date = TEST_TODAY.plusDays(1);

        Availability current = persistAvailability(  // => Should be excluded when updating
                (PetSitter) sitter, date,
                LocalTime.of(9, 0), LocalTime.of(12, 0));

        persistAvailability(
                (PetSitter) sitter, date,
                LocalTime.of(14, 0), LocalTime.of(17, 0));

        flushAndClear();

        // Act: Attempt to extend current availability until 15:00
        boolean result = availabilityDAO.isAvailabilityOverlapping(
                current.getId(),  // => to be excluded
                sitter.getId(),
                date,
                LocalTime.of(9, 0),
                LocalTime.of(15, 0)
        );

        // Assert
        assertThat(result).isTrue();
    }

    // TODO: Test Entity Attribute (DB Constraints)


    private Availability persistAvailability(final PetSitter provider,
                                             final LocalDate date,
                                             final LocalTime start,
                                             final LocalTime end)
    {
        Availability availability = new Availability();
        availability.setServiceProvider(provider);
        availability.setAvailableDate(date);
        availability.setStartTime(start);
        availability.setEndTime(end);

        testEntityManager.persistAndFlush(availability);
        return availability;
    }

    private void flushAndClear()
    {
        testEntityManager.flush();
        testEntityManager.clear();
    }
}
