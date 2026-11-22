package coursehub.practice6.numbers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Task05Test {
    @Test
    @DisplayName("new BigDecimal(double) хранит двоичный шум")
    void doubleConstructorKeepsNoise() {
        String plain = DecimalDemo.fromDouble(0.1).toPlainString();
        assertTrue(plain.length() > 20);
        assertNotEquals("0.1", plain);
    }

    @Test
    @DisplayName("valueOf(double) совпадает со строковым значением")
    void valueOfMatchesString() {
        assertEquals(DecimalDemo.fromString("0.1"), DecimalDemo.valueOf(0.1));
    }

    @Test
    @DisplayName("Шумное и точное значения 0.1 не равны")
    void noisyAndExactValuesDiffer() {
        assertNotEquals(new BigDecimal(0.1), DecimalDemo.valueOf(0.1));
        assertEquals(new BigDecimal("0.1"), DecimalDemo.valueOf(0.1));
    }

    @Test
    @DisplayName("double теряет точность, BigDecimal не теряет")
    void doubleAdditionLosesPrecision() {
        assertNotEquals(0.3, 0.1 + 0.2);
        assertEquals(new BigDecimal("0.3"), new BigDecimal("0.1").add(new BigDecimal("0.2")));
    }

    @Test
    @DisplayName("Стоимость трёх курсов округляется до 549.48")
    void courseTotalIsRounded() {
        assertEquals(new BigDecimal("549.48"), DecimalDemo.total(courses(), RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Шумные копейки видны в полном представлении")
    void noisyKopecksAreVisible() {
        assertTrue(DecimalDemo.fromDouble(0.1).toPlainString().startsWith("0.1000000000000000055"));
    }

    private static List<BigDecimal> courses() {
        return List.of(
                new BigDecimal("199.99"),
                new BigDecimal("249.50"),
                new BigDecimal("99.99")
        );
    }
}
