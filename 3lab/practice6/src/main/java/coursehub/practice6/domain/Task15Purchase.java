package coursehub.practice6.domain;

import java.time.Instant;

record Purchase(CourseOffering offering, Money paid, Instant purchasedAt) {

    Purchase {
        if (offering == null || paid == null || purchasedAt == null) {
            throw new IllegalArgumentException("purchase fields are required");
        }
        if (!paid.currency().equals(offering.price().currency())) {
            throw new IllegalArgumentException("currency mismatch: "
                    + offering.price().currency() + " vs " + paid.currency());
        }
    }
}
