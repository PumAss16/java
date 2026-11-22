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

@DisplayName("Задание 9. Границы продолжительности")
class Task09Test {

    private Task00Course course(int hours) {
        return new Task00Course(Task10CourseCode.of("CS-101"), "Java", hours, 30, 0, Task13CourseStatus.ACTIVE, null);
    }

    @ParameterizedTest(name = "duration={0} -> accepted={1}")
    @CsvSource({"3,false", "4,true", "160,true", "161,false"})
    @DisplayName("Точки вокруг границ")
    void boundaries(int hours, boolean accepted) {
        if (accepted) {
            Task00Course course = assertDoesNotThrow(() -> course(hours));
            assertEquals(hours, course.hours());
        } else {
            Task02ValidationException e = assertThrows(Task02ValidationException.class, () -> course(hours));
            assertTrue(e.getMessage().contains(String.valueOf(hours)));
        }
    }

    @Test
    @DisplayName("Внутреннее значение 40 ничего не говорит о границах")
    void typicalValueDoesNotPinTheContract() {
        assertAll(
                () -> assertDoesNotThrow(() -> course(40)),
                () -> assertThrows(Task02ValidationException.class, () -> course(Task00Course.MIN_HOURS - 1)),
                () -> assertThrows(Task02ValidationException.class, () -> course(Task00Course.MAX_HOURS + 1)));
    }
}
