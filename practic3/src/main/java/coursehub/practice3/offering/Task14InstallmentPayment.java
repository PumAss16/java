package coursehub.practice3.offering;

import java.math.BigDecimal;
import java.util.Objects;

public final class Task14InstallmentPayment implements Task14PaymentPolicy {

    private final BigDecimal fee;

    public Task14InstallmentPayment(BigDecimal fee) {
        Objects.requireNonNull(fee, "fee");
        if (fee.signum() < 0) {
            throw new IllegalArgumentException("fee must be >= 0");
        }
        this.fee = fee;
    }

    @Override
    public String name() {
        return "Installment";
    }

    @Override
    public BigDecimal total(BigDecimal basePrice) {
        return Objects.requireNonNull(basePrice, "basePrice").add(fee);
    }
}
