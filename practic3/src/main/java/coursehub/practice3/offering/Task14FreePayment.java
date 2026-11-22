package coursehub.practice3.offering;

import java.math.BigDecimal;

public final class Task14FreePayment implements Task14PaymentPolicy {

    @Override
    public String name() {
        return "Free";
    }

    @Override
    public BigDecimal total(BigDecimal basePrice) {
        return BigDecimal.ZERO;
    }
}
