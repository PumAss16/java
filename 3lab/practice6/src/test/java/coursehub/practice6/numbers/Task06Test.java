package coursehub.practice6.numbers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Task06Test {
    @Test
    @DisplayName("Сто рублей делятся на 33.33, 33.33 и 33.34")
    void hundredIsSplitExactly() {
        List<BigDecimal> parts = DiscountAllocator.allocate(
                new BigDecimal("100.00"),
                weights("33.33", "33.33", "33.34"),
                RoundingMode.HALF_UP
        );
        assertEquals(List.of(
                new BigDecimal("33.33"),
                new BigDecimal("33.33"),
                new BigDecimal("33.34")
        ), parts);
        assertEquals(new BigDecimal("100.00"), sum(parts));
    }

    @Test
    @DisplayName("Остаток 0.10 уходит на меньший индекс")
    void remainderGoesToLowerIndex() {
        List<BigDecimal> parts = DiscountAllocator.allocate(
                new BigDecimal("0.10"),
                weights("1.00", "1.00", "1.00"),
                RoundingMode.HALF_UP
        );
        assertEquals(List.of(
                new BigDecimal("0.04"),
                new BigDecimal("0.03"),
                new BigDecimal("0.03")
        ), parts);
    }

    @Test
    @DisplayName("Повторный вызов возвращает тот же список")
    void allocationIsDeterministic() {
        List<BigDecimal> first = DiscountAllocator.allocate(
                new BigDecimal("0.10"),
                weights("1.00", "1.00", "1.00"),
                RoundingMode.HALF_UP
        );
        List<BigDecimal> second = DiscountAllocator.allocate(
                new BigDecimal("0.10"),
                weights("1.00", "1.00", "1.00"),
                RoundingMode.HALF_UP
        );
        assertEquals(first, second);
    }

    @Test
    @DisplayName("Пять копеек делятся пропорционально весам")
    void biggerWeightGetsBiggerPart() {
        List<BigDecimal> parts = DiscountAllocator.allocate(
                new BigDecimal("0.05"),
                weights("1.00", "2.00"),
                RoundingMode.HALF_UP
        );
        assertEquals(new BigDecimal("0.05"), sum(parts));
        assertTrue(parts.get(1).compareTo(parts.get(0)) > 0);
    }

    @Test
    @DisplayName("Пустой список весов запрещён")
    void emptyWeightsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DiscountAllocator.allocate(new BigDecimal("1.00"), List.of(), RoundingMode.HALF_UP)
        );
    }

    @Test
    @DisplayName("Отрицательные входы запрещены")
    void negativeInputsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DiscountAllocator.allocate(new BigDecimal("-1.00"), weights("1.00"), RoundingMode.HALF_UP)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> DiscountAllocator.allocate(new BigDecimal("1.00"), weights("-1.00"), RoundingMode.HALF_UP)
        );
    }

    private static List<BigDecimal> weights(String... values) {
        List<BigDecimal> result = new ArrayList<>();
        for (String value : values) {
            result.add(new BigDecimal(value));
        }
        return result;
    }

    private static BigDecimal sum(List<BigDecimal> parts) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal part : parts) {
            total = total.add(part);
        }
        return total;
    }
}
