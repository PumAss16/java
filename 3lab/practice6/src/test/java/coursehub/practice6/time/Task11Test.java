package coursehub.practice6.time;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.util.TimeZone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task11Test {

    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");
    private static final ZoneId UTC = ZoneId.of("UTC");

    private static WebinarSchedule webinar() {
        return WebinarSchedule.of("2024-03-31T08:00:00Z", "Europe/Berlin");
    }

    @Test
    @DisplayName("время в UTC")
    void formatsInUtc() {
        assertEquals("31.03.2024 08:00 UTC", webinar().formatted(UTC));
    }

    @Test
    @DisplayName("время организатора в Берлине")
    void formatsInBerlin() {
        assertEquals("31.03.2024 10:00 CEST", webinar().formatted(BERLIN));
        assertEquals("31.03.2024 10:00 CEST", webinar().organizerTime());
    }

    @Test
    @DisplayName("время в Токио")
    void formatsInTokyo() {
        assertEquals("31.03.2024 17:00 JST", webinar().formatted(TOKYO));
    }

    @Test
    @DisplayName("момент одинаков во всех зонах")
    void sameInstantInEveryZone() {
        WebinarSchedule schedule = webinar();
        assertEquals(schedule.moment(), schedule.in(UTC).toInstant());
        assertEquals(schedule.moment(), schedule.in(BERLIN).toInstant());
        assertEquals(schedule.moment(), schedule.in(TOKYO).toInstant());
    }

    @Test
    @DisplayName("часовой пояс по умолчанию не влияет")
    void defaultTimeZoneDoesNotMatter() {
        TimeZone original = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
            WebinarSchedule schedule = webinar();
            assertEquals("31.03.2024 10:00 CEST", schedule.formatted(BERLIN));
            assertEquals("31.03.2024 08:00 UTC", schedule.formatted(UTC));
        } finally {
            TimeZone.setDefault(original);
        }
    }
}
