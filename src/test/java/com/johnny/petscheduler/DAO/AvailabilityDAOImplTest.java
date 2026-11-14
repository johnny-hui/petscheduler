package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.Availability;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.security.user.UserRole;
import com.johnny.petscheduler.utils.TestInitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(AvailabilityDAOImpl.class)
class AvailabilityDAOImplTest {

    @Autowired
    private AvailabilityDAOImpl availabilityDAO;

    @Autowired
    private TestEntityManager testEntityManager;

    private User sitter;
    private User otherSitter;

    private static final LocalDate TODAY =
            LocalDate.of(2026, 11, 13);

    @BeforeEach
    void setUp() {
        sitter = TestInitUtil.createMockUser(UserRole.PET_SITTER);
        sitter.setEmail("john1@outlook.com");

        otherSitter = TestInitUtil.createMockUser(UserRole.PET_SITTER);
        otherSitter.setEmail("john2@outlook.com");

        testEntityManager.persistAndFlush(sitter);
        testEntityManager.persistAndFlush(otherSitter);
    }

    @Test
    void create_shouldPersistAvailabilitySuccessfully()
    {
        // Arrange
        Availability availability = new Availability();
        availability.setServiceProvider(sitter);
        availability.setAvailableDate(TODAY.plusDays(1));
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
                .isEqualTo(TODAY.plusDays(1));
        assertThat(persisted.getStartTime())
                .isEqualTo(LocalTime.of(9, 0));
        assertThat(persisted.getEndTime())
                .isEqualTo(LocalTime.of(12, 0));
    }

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
