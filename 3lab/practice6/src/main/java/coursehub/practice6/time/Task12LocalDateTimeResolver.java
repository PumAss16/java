package coursehub.practice6.time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

class LocalDateTimeResolver {

    static boolean isGap(LocalDateTime local, ZoneId zone) {
        return zone.getRules().getValidOffsets(local).isEmpty();
    }

    static boolean isOverlap(LocalDateTime local, ZoneId zone) {
        return zone.getRules().getValidOffsets(local).size() > 1;
    }

    static ZonedDateTime resolve(LocalDateTime local, ZoneId zone) {
        return ZonedDateTime.of(local, zone);
    }

    static ZonedDateTime addHour(ZonedDateTime moment) {
        return moment.plusHours(1);
    }

    static ZonedDateTime addDay(ZonedDateTime moment) {
        return moment.plusDays(1);
    }
}
