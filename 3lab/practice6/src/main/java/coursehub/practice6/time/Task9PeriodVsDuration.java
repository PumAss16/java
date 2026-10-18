package coursehub.practice6.time;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

class PeriodVsDuration {

    static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    static Instant plusCalendarDay(Instant start, ZoneId zone) {
        return start.atZone(zone).plusDays(1).toInstant();
    }

    static Instant plusTwentyFourHours(Instant start) {
        return start.plus(Duration.ofDays(1));
    }

    static Duration lengthOfCalendarDay(Instant start, ZoneId zone) {
        return Duration.between(start, plusCalendarDay(start, zone));
    }
}
