package coursehub.practice3.offering;

import java.math.BigDecimal;
import java.util.Objects;

public final class Task14SubscriptionPayment implements Task14PaymentPolicy {

    private static final BigDecimal MONTHS = BigDecimal.valueOf(6);

    private final BigDecimal monthly;

    public Task14SubscriptionPayment(BigDecimal monthly) {
        Objects.requireNonNull(monthly, "monthly");
        if (monthly.signum() < 0) {
            throw new IllegalArgumentException("monthly must be >= 0");
        }
        this.monthly = monthly;
    }

    @Override
    public String name() {
        return "Subscription";
    }

    @Override
    public BigDecimal total(BigDecimal basePrice) {
        return monthly.multiply(MONTHS);
    }
}
