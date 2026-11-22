package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Задание 6. NumberFormatException -> CourseImportException")
class Task06Test {

    private final Task06CourseImporter importer = new Task06CourseImporter();

    @Test
    @DisplayName("Корректная запись разбирается")
    void parsesValidRecord() throws Task06CourseImportException {
        Task00Course course = importer.parseCourse("cs_101;Java Basics;30;40", 1);

        assertAll(
                () -> assertEquals("CS-101", course.code().value()),
                () -> assertEquals("Java Basics", course.title()),
                () -> assertEquals(30, course.capacity()),
                () -> assertEquals(40, course.hours()));
    }

    @Test
    @DisplayName("NumberFormatException переведена: номер записи 3 и cause сохранены")
    void translatesNumberFormatException() {
        Task06CourseImportException e = assertThrows(Task06CourseImportException.class,
                () -> importer.parseCourse("CS-101;Java;30;oops", 3));

        assertAll(
                () -> assertEquals(3, e.recordNumber()),
                () -> assertEquals(2, e.processed()),
                () -> assertEquals("CH-3001", e.code()),
                () -> assertInstanceOf(NumberFormatException.class, e.getCause()),
                () -> assertTrue(e.getCause().getMessage().contains("oops")),
                () -> assertTrue(e.getMessage().contains("3")));
    }

    @Test
    @DisplayName("Неверное число полей переводится так же")
    void translatesFieldCount() {
        Task06CourseImportException e = assertThrows(Task06CourseImportException.class,
                () -> importer.parseCourse("CS-101;Java", 7));

        assertAll(
                () -> assertEquals(7, e.recordNumber()),
                () -> assertInstanceOf(NumberFormatException.class, e.getCause()));
    }

    @Test
    @DisplayName("Недопустимые данные домена — тоже ожидаемый отказ")
    void translatesDomainValidation() {
        Task06CourseImportException e = assertThrows(Task06CourseImportException.class,
                () -> importer.parseCourse("!!!;Java;30;40", 1));

        assertInstanceOf(Task02ValidationException.class, e.getCause());
    }

    @Test
    @DisplayName("Баг парсера (null) не переводится: Exception не ловим")
    void doesNotSwallowProgrammingErrors() {
        assertThrows(NullPointerException.class, () -> importer.parseCourse(null, 1));
    }
}
