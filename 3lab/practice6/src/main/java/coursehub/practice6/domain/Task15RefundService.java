package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

class RefundService {

    Money refund(Purchase purchase, Instant now, RoundingMode mode) {
        WebinarSeriesPlan plan = purchase.offering().plan();
        int total = plan.lessonCount();
        int remaining = plan.sessionsAfter(now).size();
        if (remaining == 0) {
            return new Money(BigDecimal.ZERO.setScale(2), purchase.paid().currency());
        }
        BigDecimal amount = purchase.paid().amount()
                .multiply(BigDecimal.valueOf(remaining))
                .divide(BigDecimal.valueOf(total), 2, mode);
        return new Money(amount, purchase.paid().currency());
    }
}
