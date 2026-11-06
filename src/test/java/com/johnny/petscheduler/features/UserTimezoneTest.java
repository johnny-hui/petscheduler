package com.johnny.petscheduler.features;

import com.johnny.petscheduler.utils.TimezoneUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserTimezoneTest
{
    @ParameterizedTest
    @ValueSource(strings = {
            "America/Vancouver",
            "America/Toronto",
            "Asia/Tokyo",
            "Europe/London",
            "UTC"
    })
    void validateTimezone_ShouldAcceptValidTimezones(final String timezone) {
        assertTrue(TimezoneUtil.isValidTimezone(timezone));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "Invalid/Timezone",
            "America/Invalid",
            "Canada",
            "Vancouver"
    })
    void validateTimezone_ShouldRejectInvalidTimezones(final String timezone) {
        assertFalse(TimezoneUtil.isValidTimezone(timezone));
    }

    @Test
    void validateTimezone_ShouldRejectNull() {
        assertFalse(TimezoneUtil.isValidTimezone(null));
    }

    @Test
    void getCurrentDate_ShouldReturnVancouverLocalDate()
    {
        // Arrange
        Clock clock = Clock.fixed(
                Instant.parse("2026-11-05T02:00:00Z"),
                ZoneOffset.UTC
        );
        ZoneId timezone = ZoneId.of("America/Vancouver");

        // Act
        LocalDate result = TimezoneUtil.getCurrentDate(timezone, clock);

        // Assert
        assertEquals(LocalDate.of(2026, 11, 4), result);
    }

    @Test
    void getCurrentTime_ShouldReturnTokyoLocalTime()
    {
        // Arrange
        Clock clock = Clock.fixed(
                Instant.parse("2026-11-05T02:00:00Z"),
                ZoneOffset.UTC
        );
        ZoneId timezone = ZoneId.of("Asia/Tokyo");

        // Act
        LocalTime result = TimezoneUtil.getCurrentTime(timezone, clock);

        // Assert
        assertEquals(LocalTime.of(11, 0), result);
    }

    @Test
    void getCurrentDate_ShouldAccountForDifferentTimezones()
    {
        // Arrange
        Clock clock = Clock.fixed(
                Instant.parse("2026-11-05T02:00:00Z"),
                ZoneOffset.UTC
        );

        // Act
        LocalDate vancouver = TimezoneUtil.getCurrentDate(
                ZoneId.of("America/Vancouver"), clock
        );
        LocalDate tokyo = TimezoneUtil.getCurrentDate(
                ZoneId.of("Asia/Tokyo"), clock
        );

        // Assert
        assertEquals(LocalDate.of(2026, 11, 4), vancouver);
        assertEquals(LocalDate.of(2026, 11, 5), tokyo);
    }

    @Test
    void timezone_ShouldHandleDaylightSavingTime()
    {
        // Arrange
        ZoneId timezone = ZoneId.of("America/Vancouver");

        // Act
        ZonedDateTime before = Instant.parse("2026-03-08T09:30:00Z")
                .atZone(timezone);

        ZonedDateTime after = Instant.parse("2026-03-08T10:30:00Z")
                .atZone(timezone);

        // Assert
        assertEquals(LocalTime.of(1, 30), before.toLocalTime());
        assertEquals(LocalTime.of(3, 30), after.toLocalTime());

        assertEquals(ZoneOffset.ofHours(-8), before.getOffset());
        assertEquals(ZoneOffset.ofHours(-7), after.getOffset());
    }
}
