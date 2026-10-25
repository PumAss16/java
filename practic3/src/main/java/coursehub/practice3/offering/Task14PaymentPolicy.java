package coursehub.practice3.offering;

import java.math.BigDecimal;

public interface Task14PaymentPolicy {

    String name();

    BigDecimal total(BigDecimal basePrice);
}
