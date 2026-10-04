package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Задание 9. Границы продолжительности курса [4, 160]:
 * 3 (ниже минимума), 4 (минимум), 160 (максимум), 161 (выше максимума).
 * «Обычное» значение 40 контракт не фиксирует: оно проходит и при отсутствующей,
 * сдвинутой или инвертированной проверке границ. Границу задают только края
 * диапазона и первые недопустимые значения.
 */
@DisplayName("Задание 9. Границы продолжительности")
class Task09Test {

    private Course course(int hours) {
        return new Course(CourseCode.of("CS-101"), "Java", hours, 30, 0, CourseStatus.ACTIVE, null);
    }

    @ParameterizedTest(name = "duration={0} -> accepted={1}")
    @CsvSource({"3,false", "4,true", "160,true", "161,false"})
    @DisplayName("Точки вокруг границ")
    void boundaries(int hours, boolean accepted) {
        if (accepted) {
            Course course = assertDoesNotThrow(() -> course(hours));
            assertEquals(hours, course.hours());
        } else {
            ValidationException e = assertThrows(ValidationException.class, () -> course(hours));
            assertTrue(e.getMessage().contains(String.valueOf(hours)));
        }
    }

    @Test
    @DisplayName("Внутреннее значение 40 ничего не говорит о границах")
    void typicalValueDoesNotPinTheContract() {
        assertAll(
                () -> assertDoesNotThrow(() -> course(40)),
                () -> assertThrows(ValidationException.class, () -> course(Course.MIN_HOURS - 1)),
                () -> assertThrows(ValidationException.class, () -> course(Course.MAX_HOURS + 1)));
    }
}
