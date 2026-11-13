package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.availability.InvalidAvailabilityTimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test contains test cases for validation of Availability date and times.
 */
public class AvailabilityUtilTest
{
    private static final String TEST_TIMEZONE = "America/Vancouver";

    private User serviceProvider;

    @BeforeEach
    void setUp() {
        serviceProvider = new PetSitter();
        serviceProvider.setId(1L);
        serviceProvider.setTimezone(TEST_TIMEZONE);
    }

    @ParameterizedTest
    @CsvSource({
            // Valid: start before end, 15-min increments, >= 3 hours
            "09:00, 12:00, true",
            "09:15, 12:15, true",
            "10:30, 14:45, true",
            "09:00, 17:00, true",

            // Invalid: start time >= end time
            "15:00, 12:00, false",
            "12:00, 12:00, false",

            // Invalid: not on 15-minute increments
            "09:10, 12:10, false",
            "09:00, 12:20, false",
            "09:05, 12:15, false",

            // Invalid: duration < 3 hours
            "09:00, 11:45, false",
            "10:15, 13:00, false",
            "09:00, 09:15, false"
    })
    void validateAvailability_whenCheckingTimeConstraints(
            LocalTime startTime,
            LocalTime endTime,
            boolean shouldBeValid
    ) {
        // Arrange
        Clock clock = createFixedClock(
                TEST_TIMEZONE,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(8, 0)
        );

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.now(clock).plusDays(1),
                startTime,
                endTime
        );

        // Act & Assert
        if (shouldBeValid) {
            assertDoesNotThrow(
                    () -> AvailabilityUtil.validateAvailabilityRequest(
                            serviceProvider,
                            request,
                            clock
                    )
            );
        } else {
            assertThrows(
                    InvalidAvailabilityTimeException.class,
                    () -> AvailabilityUtil.validateAvailabilityRequest(
                            serviceProvider,
                            request,
                            clock
                    )
            );
        }
    }

    @ParameterizedTest
    @CsvSource({
            "09:00, 12:00, true",
            "09:00, 12:15, true",
            "09:00, 11:45, false",
            "10:15, 13:00, false"
    })
    void validateAvailability_whenCheckingMinimumDuration(
            LocalTime startTime,
            LocalTime endTime,
            boolean shouldBeValid
    ) {
        assertTimeValidation(startTime, endTime, shouldBeValid);
    }

    @ParameterizedTest
    @CsvSource({
            "09:00, 12:00, true",
            "12:00, 12:00, false",
            "15:00, 12:00, false"
    })
    void validateAvailability_whenCheckingStartBeforeEnd(
            LocalTime startTime,
            LocalTime endTime,
            boolean shouldBeValid
    ) {
        assertTimeValidation(startTime, endTime, shouldBeValid);
    }

    @ParameterizedTest
    @CsvSource({
            "09:00, 12:00, true",
            "09:15, 12:15, true",
            "09:30, 12:30, true",
            "09:45, 12:45, true",
            "09:10, 12:10, false",
            "09:00, 12:20, false"
    })
    void validateAvailability_whenChecking15MinuteTimeIncrements(
            LocalTime startTime,
            LocalTime endTime,
            boolean shouldBeValid
    ) {
        assertTimeValidation(startTime, endTime, shouldBeValid);
    }



    private void assertTimeValidation(
            LocalTime startTime,
            LocalTime endTime,
            boolean shouldBeValid
    ) {
        Clock clock = createFixedClock(
                TEST_TIMEZONE,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(8, 0)
        );

        AvailabilityRequestDTO request = new AvailabilityRequestDTO(
                LocalDate.now(clock).plusDays(1),
                startTime,
                endTime
        );

        if (shouldBeValid) {
            assertDoesNotThrow(
                    () -> AvailabilityUtil.validateAvailabilityRequest(
                            serviceProvider,
                            request,
                            clock
                    )
            );
        } else {
            assertThrows(
                    InvalidAvailabilityTimeException.class,
                    () -> AvailabilityUtil.validateAvailabilityRequest(
                            serviceProvider,
                            request,
                            clock
                    )
            );
        }
    }

    private Clock createFixedClock(final String timezone,
                                   final LocalDate date,
                                   final LocalTime time)
    {
        ZoneId zone = ZoneId.of(timezone);
        return Clock.fixed(
                LocalDateTime.of(date, time)
                        .atZone(zone)
                        .toInstant(),
                zone
        );
    }
}
