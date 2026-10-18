package coursehub.practice6.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task09Test {

    private static final ZoneId BERLIN = PeriodVsDuration.BERLIN;

    private static Instant berlinStart(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 30).atZone(BERLIN).toInstant();
    }

    @Test
    @DisplayName("весенний календарный день короче на час")
    void springCalendarDayHasTwentyThreeHours() {
        Instant start = berlinStart(2024, 3, 31);
        assertEquals(Duration.ofHours(23), PeriodVsDuration.lengthOfCalendarDay(start, BERLIN));
        assertEquals(Duration.ofHours(24), Duration.between(start, PeriodVsDuration.plusTwentyFourHours(start)));
        assertNotEquals(
                PeriodVsDuration.plusCalendarDay(start, BERLIN),
                PeriodVsDuration.plusTwentyFourHours(start));
    }

    @Test
    @DisplayName("осенний календарный день длиннее на час")
    void autumnCalendarDayHasTwentyFiveHours() {
        Instant start = berlinStart(2024, 10, 27);
        assertEquals(Duration.ofHours(25), PeriodVsDuration.lengthOfCalendarDay(start, BERLIN));
        assertEquals(Duration.ofHours(24), Duration.between(start, PeriodVsDuration.plusTwentyFourHours(start)));
    }

    @Test
    @DisplayName("календарный день сохраняет локальное время")
    void calendarDayKeepsLocalTime() {
        Instant start = berlinStart(2024, 3, 31);
        assertEquals(
                LocalDateTime.of(2024, 4, 1, 0, 30),
                PeriodVsDuration.plusCalendarDay(start, BERLIN).atZone(BERLIN).toLocalDateTime());
        assertEquals(
                LocalDateTime.of(2024, 4, 1, 1, 30),
                PeriodVsDuration.plusTwentyFourHours(start).atZone(BERLIN).toLocalDateTime());
    }

    @Test
    @DisplayName("в UTC календарный день равен суткам")
    void calendarDayEqualsDayInUtc() {
        Instant start = Instant.parse("2024-03-31T00:00:00Z");
        assertEquals(
                PeriodVsDuration.plusTwentyFourHours(start),
                PeriodVsDuration.plusCalendarDay(start, ZoneOffset.UTC));
    }
}
