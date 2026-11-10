package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Задание 10. Нормализация кодов курса")
class Task10Test {

    @ParameterizedTest(name = "''{0}'' -> ''{1}''")
    @CsvSource({
            "'  cs-101 ', CS-101",
            "cs101, CS-101",
            "CS_101, CS-101",
            "'cs 101', CS-101",
            "CS--101, CS-101",
            "'  java-202  ', JAVA-202"})
    @DisplayName("Допустимые коды нормализуются")
    void normalizesValidCodes(String raw, String expected) {
        assertEquals(expected, Task10CourseCode.of(raw).value());
    }

    @ParameterizedTest(name = "''{0}'' -> отказ ({1})")
    @CsvSource({
            "'', пустая строка",
            "'   ', только пробелы",
            "C-101, одна буква в префиксе",
            "ABCDE-101, пять букв в префиксе",
            "CS-12, две цифры вместо трёх",
            "CS-1234, четыре цифры вместо трёх",
            "101-CS, цифры перед буквами",
            "CS-ABC, буквы в номере",
            "CS/101, недопустимый разделитель"})
    @DisplayName("Недопустимые коды отвергаются")
    void rejectsInvalidCodes(String raw, String why) {
        Task02ValidationException e = assertThrows(Task02ValidationException.class, () -> Task10CourseCode.of(raw), why);

        assertTrue(e.getMessage().startsWith("invalid course code"), why);
    }

    @Test
    @DisplayName("null — тоже неверный аргумент")
    void rejectsNull() {
        assertThrows(Task02ValidationException.class, () -> Task10CourseCode.of(null));
    }

    @Test
    @DisplayName("Нормализация идемпотентна, equals — по значению")
    void normalizationIsIdempotent() {
        Task10CourseCode once = Task10CourseCode.of("cs 101");

        assertAll(
                () -> assertEquals(once, Task10CourseCode.of(once.value())),
                () -> assertEquals("CS-101", once.toString()));
    }
}
