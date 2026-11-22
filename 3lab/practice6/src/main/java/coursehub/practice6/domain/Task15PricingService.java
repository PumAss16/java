package coursehub.practice6.domain;

import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

class PricingService {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm z");

    Money priceAt(CourseOffering offering, Instant moment, RoundingMode mode) {
        EarlyBirdPolicy policy = new EarlyBirdPolicy(offering.earlyBirdDeadline(), offering.earlyBirdPercent());
        return policy.priceAt(offering.price(), moment, mode);
    }

    String displaySession(WebinarSeriesPlan plan, Instant session, ZoneId viewerZone) {
        return FORMAT.format(session.atZone(viewerZone));
    }
}
