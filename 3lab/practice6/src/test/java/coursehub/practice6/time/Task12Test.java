package coursehub.practice6.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task12Test {

    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    @Test
    @DisplayName("пропуск в весеннюю ночь")
    void springGap() {
        LocalDateTime local = LocalDateTime.of(2024, 3, 31, 2, 30);
        assertTrue(LocalDateTimeResolver.isGap(local, BERLIN));
        assertFalse(LocalDateTimeResolver.isOverlap(local, BERLIN));
    }

    @Test
    @DisplayName("пропуск сдвигается вперёд")
    void gapMovesForward() {
        ZonedDateTime resolved = LocalDateTimeResolver.resolve(LocalDateTime.of(2024, 3, 31, 2, 30), BERLIN);
        assertEquals(LocalDateTime.of(2024, 3, 31, 3, 30), resolved.toLocalDateTime());
        assertEquals(ZoneOffset.ofHours(2), resolved.getOffset());
        assertEquals(Instant.parse("2024-03-31T01:30:00Z"), resolved.toInstant());
    }

    @Test
    @DisplayName("повтор в осеннюю ночь")
    void autumnOverlap() {
        LocalDateTime local = LocalDateTime.of(2024, 10, 27, 2, 30);
        assertTrue(LocalDateTimeResolver.isOverlap(local, BERLIN));
        assertFalse(LocalDateTimeResolver.isGap(local, BERLIN));
    }

    @Test
    @DisplayName("при повторе берётся ранний offset")
    void overlapUsesEarlierOffset() {
        ZonedDateTime resolved = LocalDateTimeResolver.resolve(LocalDateTime.of(2024, 10, 27, 2, 30), BERLIN);
        assertEquals(ZoneOffset.ofHours(2), resolved.getOffset());
        assertEquals(Instant.parse("2024-10-27T00:30:00Z"), resolved.toInstant());
        assertEquals(LocalDateTime.of(2024, 10, 27, 2, 30), resolved.toLocalDateTime());
    }

    @Test
    @DisplayName("плюс час перескакивает пропуск")
    void addHourSkipsGap() {
        ZonedDateTime before = ZonedDateTime.of(2024, 3, 31, 1, 30, 0, 0, BERLIN);
        ZonedDateTime after = LocalDateTimeResolver.addHour(before);
        assertEquals(LocalDateTime.of(2024, 3, 31, 3, 30), after.toLocalDateTime());
        assertEquals(ZoneOffset.ofHours(2), after.getOffset());
    }

    @Test
    @DisplayName("плюс день сохраняет локальное время")
    void addDayKeepsLocalTime() {
        ZonedDateTime moment = ZonedDateTime.of(2024, 3, 31, 0, 30, 0, 0, BERLIN);
        ZonedDateTime next = LocalDateTimeResolver.addDay(moment);
        assertEquals(LocalDateTime.of(2024, 4, 1, 0, 30), next.toLocalDateTime());
        assertEquals(Duration.ofHours(23), Duration.between(moment, next));
    }
}
