package coursehub.practice6.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

class DecimalDemo {
    static BigDecimal fromDouble(double value) {
        return new BigDecimal(value);
    }

    static BigDecimal valueOf(double value) {
        return BigDecimal.valueOf(value);
    }

    static BigDecimal fromString(String value) {
        return new BigDecimal(value);
    }

    static BigDecimal total(List<BigDecimal> amounts, RoundingMode mode) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            sum = sum.add(amount);
        }
        return sum.setScale(2, mode);
    }
}
