package com.johnny.petscheduler.entity;

import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.security.user.UserRole;
import com.johnny.petscheduler.utils.TestInitUtil;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This tests DB constraints on the Availability entity.
 */
@DataJpaTest
public class AvailabilityPersistenceTest
{
    @Autowired
    private TestEntityManager testEntityManager;

    private User sitter;
    private static final LocalDate TEST_DATE =
            LocalDate.of(2026, 11, 18);

    @BeforeEach
    void setUp()
    {
        sitter = TestInitUtil.createMockUser(UserRole.PET_SITTER, "john1@outlook.com");
        testEntityManager.persistAndFlush(sitter);
    }

    @Test
    void persist_shouldSucceedWhenAllRequiredFieldsArePresent()
    {
        // Arrange
        Availability availability = createValidAvailability();

        // Act
        testEntityManager.persistAndFlush(availability);
        Long id = availability.getId();
        testEntityManager.clear();

        // Assert
        Availability persisted = testEntityManager.find(Availability.class, id);

        assertThat(persisted)
                .isNotNull();
        assertThat(persisted.getId())
                .isEqualTo(id);
        assertThat(persisted.getAvailableDate())
                .isEqualTo(TEST_DATE);
        assertThat(persisted.getStartTime())
                .isEqualTo(LocalTime.of(9, 0));
        assertThat(persisted.getEndTime())
                .isEqualTo(LocalTime.of(12, 0));
        assertThat(persisted.getServiceProvider().getId())
                .isEqualTo(sitter.getId());
    }

    @ParameterizedTest
    @EnumSource(RequiredField.class)
    void persist_shouldRejectNullRequiredFields(final RequiredField field)
    {
        // Arrange
        Availability availability = createValidAvailability();
        switch (field) {
            case SERVICE_PROVIDER ->
                    availability.setServiceProvider(null);
            case AVAILABLE_DATE ->
                    availability.setAvailableDate(null);
            case START_TIME ->
                    availability.setStartTime(null);
            case END_TIME ->
                    availability.setEndTime(null);
        }

        // Act & Assert
        assertThatThrownBy(() -> {
            testEntityManager.persistAndFlush(availability);
        }).isInstanceOfAny(
                org.hibernate.PropertyValueException.class,
                jakarta.persistence.PersistenceException.class,
                org.springframework.dao.DataIntegrityViolationException.class
        );
    }


    @Test
    void update_shouldIncrementVersion() {
        // Arrange
        Availability availability = createValidAvailability();
        testEntityManager.persistAndFlush(availability);

        Long id = availability.getId();
        Long initialVersion = availability.getVersion();

        // Act
        availability.setEndTime(LocalTime.of(13, 0));
        testEntityManager.flush();
        testEntityManager.clear();

        // Assert
        Availability updated = testEntityManager.find(Availability.class, id);
        assertThat(initialVersion)
                .isNotNull();
        assertThat(updated.getVersion())
                .isEqualTo(initialVersion + 1);
        assertThat(updated.getEndTime())
                .isEqualTo(LocalTime.of(13, 0));
    }


    @Test
    void update_shouldRejectStaleEntityVersion() {
        // Arrange
        Availability availability = createValidAvailability();
        testEntityManager.persistAndFlush(availability);

        Long id = availability.getId();
        testEntityManager.clear();

        // First copy
        Availability firstCopy = testEntityManager.find(Availability.class, id);
        testEntityManager.clear();

        // Second copy
        Availability secondCopy = testEntityManager.find(Availability.class, id);
        testEntityManager.clear();

        // First update succeeds
        firstCopy.setEndTime(LocalTime.of(13, 0));

        testEntityManager.merge(firstCopy);
        testEntityManager.flush();
        testEntityManager.clear();

        // Second copy has an outdated version
        secondCopy.setEndTime(LocalTime.of(14, 0));

        // Act & Assert
        assertThatThrownBy(() -> {
            testEntityManager.merge(secondCopy);
            testEntityManager.flush();
        }).isInstanceOfAny(
                OptimisticLockException.class,
                org.hibernate.StaleObjectStateException.class,
                org.springframework.orm.ObjectOptimisticLockingFailureException.class
        );
    }


    private Availability createValidAvailability()
    {
        Availability availability = new Availability();

        availability.setServiceProvider(sitter);
        availability.setAvailableDate(TEST_DATE);
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(12, 0));

        return availability;
    }

    private enum RequiredField
    {
        SERVICE_PROVIDER,
        AVAILABLE_DATE,
        START_TIME,
        END_TIME
    }
}
