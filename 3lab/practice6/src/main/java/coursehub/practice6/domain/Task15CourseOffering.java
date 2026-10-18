package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.time.Instant;

record CourseOffering(String code, CourseTitle title, WebinarSeriesPlan plan, Money price,
        Instant earlyBirdDeadline, BigDecimal earlyBirdPercent) {

    CourseOffering {
        if (code == null || title == null || plan == null || price == null
                || earlyBirdDeadline == null || earlyBirdPercent == null) {
            throw new IllegalArgumentException("offering fields are required");
        }
        if (plan.lessonCount() <= 0) {
            throw new IllegalArgumentException("plan must have lessons");
        }
    }
}
