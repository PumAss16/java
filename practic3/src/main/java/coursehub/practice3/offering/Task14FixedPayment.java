package coursehub.practice3.offering;

import java.math.BigDecimal;
import java.util.Objects;

public final class Task14FixedPayment implements Task14PaymentPolicy {

    @Override
    public String name() {
        return "Fixed";
    }

    @Override
    public BigDecimal total(BigDecimal basePrice) {
        return Objects.requireNonNull(basePrice, "basePrice");
    }
}
