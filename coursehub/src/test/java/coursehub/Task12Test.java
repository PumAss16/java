package coursehub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Задание 12. Убраны четыре источника хрупкости:
 * 1) общее static-состояние        -> состояние создаётся в @BeforeEach и своё у каждого теста;
 * 2) зависимость от порядка         -> @TestMethodOrder(Random) и никаких следов между тестами;
 * 3) текущая директория             -> файлы только во временной папке @TempDir;
 * 4) полный текст toString          -> проверяются поля и значимые фрагменты.
 */
@DisplayName("Задание 12. Нет хрупких тестов")
@TestMethodOrder(MethodOrderer.Random.class)
class Task12Test {

    private StringBuilder state;

    @BeforeEach
    void freshState() {
        state = new StringBuilder();
    }

    @Test
    @DisplayName("Состояние своё: тест не зависит от того, что выполнялось раньше")
    void stateIsPerTest() {
        state.append("x");

        assertEquals("x", state.toString());
    }

    @Test
    @DisplayName("Пути не зависят от текущей директории: только @TempDir")
    void usesTempDir(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("courses.csv");

        Files.writeString(file, "CS-101;Java;30;40\n");

        assertAll(
                () -> assertTrue(file.isAbsolute()),
                () -> assertEquals(1, Files.readAllLines(file).size()));
    }

    @Test
    @DisplayName("toString проверяется структурно, а не целиком")
    void checksStructureNotWholeToString() {
        EnrollmentRejectedException e =
                new EnrollmentRejectedException("S-7", "CS-101", RejectionReason.COURSE_FULL);

        assertAll(
                () -> assertTrue(e.toString().contains("CS-101")),
                () -> assertTrue(e.toString().contains("COURSE_FULL")),
                () -> assertFalse(e.toString().contains("S-7")),
                () -> assertEquals("CS-101", e.courseId()));
    }

    @Test
    @DisplayName("Значения сравниваются через equals, а не через toString")
    void comparesByEquals() {
        CourseCode compact = CourseCode.of("cs101");
        CourseCode canonical = CourseCode.of("CS-101");

        assertAll(
                () -> assertEquals(canonical, compact),
                () -> assertEquals(canonical.hashCode(), compact.hashCode()));
    }
}
