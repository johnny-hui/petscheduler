package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.DTO.availabilities.AvailabilityRequestDTO;
import com.johnny.petscheduler.entities.users.PetSitter;
import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.availability.InvalidAvailabilityDateException;
import com.johnny.petscheduler.exceptions.availability.InvalidAvailabilityTimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.*;

import static com.johnny.petscheduler.utils.AvailabilityUtil.MAX_AVAILABILITY_LIMIT_DAYS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test contains test cases for validation of Availability date and times.
 * Tests validation methods for the AvailabilityUtil and TimeUtil classes.
 *
 * @see AvailabilityUtil
 * @see TimeUtil
 */
public class AvailabilityTimeUtilTest
{
    private static final String TEST_TIMEZONE = "America/Vancouver";

    private Clock clock;
    private User serviceProvider;

    @BeforeEach
    void setUp() {
        setTestClock(
                TEST_TIMEZONE,
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0)
        );
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

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-12-01",
            "2026-12-02",
            "2026-12-10"
    })
    void validateAvailableDateNotInPast_validDates_doesNotThrow(String date) {
        assertDoesNotThrow(() ->
                TimeUtil.validateAvailableDateNotInPast(
                        TEST_TIMEZONE,
                        LocalDate.parse(date),
                        clock,
                        new InvalidAvailabilityDateException("Invalid date")
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-11-30",
            "2026-11-01",
            "2025-12-01"
    })
    void validateAvailableDateNotInPast_pastDates_throwsException(String date) {
        // Paused date = 2026-12-01, 10:00
        assertThrows(
                InvalidAvailabilityDateException.class,
                () -> TimeUtil.validateAvailableDateNotInPast(
                        TEST_TIMEZONE,
                        LocalDate.parse(date),
                        clock,
                        new InvalidAvailabilityDateException("Invalid date")
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "09:00",
            "09:45",
            "10:00"
    })
    void validateStartTimeInFutureIfToday_pastOrCurrentTime_throwsException(String time) {
        // Paused time = 10:00
        assertThrows(
                InvalidAvailabilityTimeException.class,
                () -> TimeUtil.validateStartTimeInFutureIfToday(
                        TEST_TIMEZONE,
                        LocalDate.of(2026, 12, 1),
                        LocalTime.parse(time),
                        clock,
                        new InvalidAvailabilityTimeException("Invalid start time")
                )
        );
    }

    @Test
    void validateStartTimeInFutureIfToday_futureDate_doesNotThrow() {
        // Paused time = 10:00
        assertDoesNotThrow(() ->
               TimeUtil.validateStartTimeInFutureIfToday(
                        TEST_TIMEZONE,
                        LocalDate.of(2026, 12, 2),
                        LocalTime.of(9, 0),
                        clock,
                        new InvalidAvailabilityTimeException("Invalid start time")
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "09:00, 12:00",
            "10:00, 17:00",
            "00:00, 23:45"
    })
    void validateStartBeforeEnd_validRange_doesNotThrow(String start, String end)
    {
        assertDoesNotThrow(() ->
                TimeUtil.validateStartBeforeEnd(
                        LocalTime.parse(start),
                        LocalTime.parse(end),
                        new InvalidAvailabilityTimeException("Invalid time range")
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "12:00, 09:00",
            "10:00, 10:00",
            "23:00, 01:00"
    })
    void validateStartBeforeEnd_invalidRange_throwsException(String start, String end)
    {
        assertThrows(
                InvalidAvailabilityTimeException.class,
                () -> TimeUtil.validateStartBeforeEnd(
                        LocalTime.parse(start),
                        LocalTime.parse(end),
                        new InvalidAvailabilityTimeException("Invalid time range")
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-12-01",
            "2026-12-10",
            "2027-01-01",
            "2027-02-28",
            "2027-03-01"
    })
    void validateWithinMaxFutureDate_within90Days_doesNotThrow(String date) {
        assertDoesNotThrow(() ->
                TimeUtil.validateWithinMaxFutureDate(
                        TEST_TIMEZONE,
                        LocalDate.parse(date),
                        MAX_AVAILABILITY_LIMIT_DAYS,
                        clock,
                        new InvalidAvailabilityDateException("Date exceeds limit")
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2027-03-02",
            "2027-03-15",
            "2027-04-01",
            "2028-01-01"
    })
    void validateWithinMaxFutureDate_beyond90Days_throwsException(
            String date
    ) {
        assertThrows(
                InvalidAvailabilityDateException.class,
                () -> TimeUtil.validateWithinMaxFutureDate(
                        TEST_TIMEZONE,
                        LocalDate.parse(date),
                        MAX_AVAILABILITY_LIMIT_DAYS,
                        clock,
                        new InvalidAvailabilityDateException("Date exceeds limit")
                )
        );
    }

    @Test
    void validateAvailableDateNotInPast_usesUserTimezone() {
        // Arrange
        Clock utcClock = Clock.fixed(
                Instant.parse("2026-12-02T07:30:00Z"),
                ZoneOffset.UTC
        );

        // Vancouver local time: Dec 1, 11:30 PM
        // Tokyo local time: Dec 2, 4:30 PM

        // Vancouver: December 1 is today
        assertDoesNotThrow(() ->
                TimeUtil.validateAvailableDateNotInPast(
                        "America/Vancouver",
                        LocalDate.of(2026, 12, 1),
                        utcClock,
                        new InvalidAvailabilityDateException("Past date")
                )
        );

        // Tokyo: December 1 is in the past
        assertThrows(
                InvalidAvailabilityDateException.class,
                () -> TimeUtil.validateAvailableDateNotInPast(
                        "Asia/Tokyo",
                        LocalDate.of(2026, 12, 1),
                        utcClock,
                        new InvalidAvailabilityDateException("Past date")
                )
        );
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

    private void setTestClock(final String timezone,
                              final LocalDate date,
                              final LocalTime time)
    {
        clock = createFixedClock(timezone, date, time);
    }
}
