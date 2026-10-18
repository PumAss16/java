package coursehub.practice6.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task14Test {

    private static final Currency EUR = Currency.getInstance("EUR");

    @Test
    @DisplayName("Сложение и вычитание в одной валюте")
    void addsAndSubtracts() {
        Money first = eur("10.00");
        Money second = eur("2.50");
        assertEquals(eur("12.50"), first.add(second));
        assertEquals(eur("7.50"), first.subtract(second));
    }

    @Test
    @DisplayName("Смешение валют запрещено")
    void rejectsCurrencyMismatch() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> eur("10.00").add(Money.of("10.00", "USD")));
        assertEquals("currency mismatch: EUR vs USD", error.getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> eur("10.00").subtract(Money.of("10.00", "USD")));
    }

    @Test
    @DisplayName("Процент округляется до копеек")
    void calculatesPercentage() {
        assertEquals(eur("10.00"), eur("100.00").percentage(new BigDecimal("10"), RoundingMode.HALF_UP));
        assertEquals(eur("3.33"), eur("33.33").percentage(new BigDecimal("10"), RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Распределение сохраняет исходную сумму")
    void allocatesWithoutLosingCents() {
        Money source = eur("10.00");
        List<Money> parts = source.allocate(3, RoundingMode.HALF_UP);
        assertEquals(List.of(eur("3.34"), eur("3.33"), eur("3.33")), parts);
        Money sum = eur("0.00");
        for (Money part : parts) {
            sum = sum.add(part);
        }
        assertEquals(source, sum);
    }

    @Test
    @DisplayName("Равенство учитывает значение, а не масштаб")
    void equalityIgnoresScale() {
        Money scaled = eur("10.00");
        Money compact = eur("10.0");
        assertEquals(scaled, compact);
        assertEquals(scaled.hashCode(), compact.hashCode());
        assertEquals("10.00 EUR", scaled.toString());
    }

    @Test
    @DisplayName("Сумма хранится без скрытого округления")
    void keepsOriginalScale() {
        Money money = new Money(new BigDecimal("10.000"), EUR);
        assertEquals(new BigDecimal("10.000"), money.amount());
    }

    @Test
    @DisplayName("Null-аргументы отклоняются")
    void rejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Money(null, EUR));
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.ONE, null));
        assertThrows(IllegalArgumentException.class, () -> Money.of(null, "EUR"));
        assertThrows(IllegalArgumentException.class, () -> Money.of("10.00", null));
        assertThrows(IllegalArgumentException.class, () -> eur("10.00").add(null));
        assertThrows(IllegalArgumentException.class, () -> eur("10.00").percentage(null, RoundingMode.HALF_UP));
        assertThrows(IllegalArgumentException.class, () -> eur("10.00").percentage(BigDecimal.TEN, null));
        assertThrows(IllegalArgumentException.class, () -> eur("10.00").allocate(0, RoundingMode.HALF_UP));
        assertThrows(IllegalArgumentException.class, () -> eur("10.00").allocate(2, null));
    }

    private static Money eur(String amount) {
        return Money.of(amount, "EUR");
    }
}
