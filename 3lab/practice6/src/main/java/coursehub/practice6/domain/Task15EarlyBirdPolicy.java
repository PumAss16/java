package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

record EarlyBirdPolicy(Instant deadline, BigDecimal percent) {

    EarlyBirdPolicy {
        if (deadline == null || percent == null) {
            throw new IllegalArgumentException("policy fields are required");
        }
    }

    boolean isActive(Instant purchaseMoment) {
        if (purchaseMoment == null) {
            throw new IllegalArgumentException("moment is required");
        }
        return purchaseMoment.isBefore(deadline);
    }

    Money apply(Money base, RoundingMode mode) {
        return base.percentage(percent, mode);
    }

    Money priceAt(Money base, Instant moment, RoundingMode mode) {
        if (isActive(moment)) {
            return base.subtract(apply(base, mode));
        }
        return base;
    }
}
