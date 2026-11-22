package coursehub.practice6.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

record WebinarSchedule(Instant moment, ZoneId organizerZone) {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm z");

    static WebinarSchedule of(String isoInstant, String zone) {
        return new WebinarSchedule(Instant.parse(isoInstant), ZoneId.of(zone));
    }

    ZonedDateTime in(ZoneId zone) {
        return moment.atZone(zone);
    }

    String formatted(ZoneId zone) {
        return in(zone).format(FORMAT);
    }

    String organizerTime() {
        return formatted(organizerZone);
    }
}
