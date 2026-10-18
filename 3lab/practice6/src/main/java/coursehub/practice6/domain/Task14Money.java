package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

class Money {

    private final BigDecimal amount;
    private final Currency currency;

    Money(BigDecimal amount, Currency currency) {
        if (amount == null || currency == null) {
            throw new IllegalArgumentException("amount and currency are required");
        }
        this.amount = amount;
        this.currency = currency;
    }

    static Money of(String amount, String currencyCode) {
        if (amount == null || currencyCode == null) {
            throw new IllegalArgumentException("amount and currency code are required");
        }
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    BigDecimal amount() {
        return amount;
    }

    Currency currency() {
        return currency;
    }

    Money add(Money other) {
        checkCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    Money subtract(Money other) {
        checkCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    Money percentage(BigDecimal percent, RoundingMode mode) {
        if (percent == null || mode == null) {
            throw new IllegalArgumentException("percent and mode are required");
        }
        BigDecimal result = amount.multiply(percent).movePointLeft(2).setScale(2, mode);
        return new Money(result, currency);
    }

    List<Money> allocate(int parts, RoundingMode mode) {
        if (parts <= 0 || mode == null) {
            throw new IllegalArgumentException("parts and mode are required");
        }
        long totalCents = amount.setScale(2, mode).movePointRight(2).longValueExact();
        long base = totalCents / parts;
        long extra = totalCents - base * parts;
        long step = Long.signum(extra);
        long withExtra = Math.abs(extra);
        List<Money> result = new ArrayList<>();
        for (int i = 0; i < parts; i++) {
            long cents = i < withExtra ? base + step : base;
            result.add(new Money(BigDecimal.valueOf(cents).movePointLeft(2), currency));
        }
        return List.copyOf(result);
    }

    private void checkCurrency(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("other money is required");
        }
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("currency mismatch: " + currency + " vs " + other.currency);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Money money)) {
            return false;
        }
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
