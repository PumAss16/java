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
        Task03EnrollmentRejectedException e =
                new Task03EnrollmentRejectedException("S-7", "CS-101", Task03RejectionReason.COURSE_FULL);

        assertAll(
                () -> assertTrue(e.toString().contains("CS-101")),
                () -> assertTrue(e.toString().contains("COURSE_FULL")),
                () -> assertFalse(e.toString().contains("S-7")),
                () -> assertEquals("CS-101", e.courseId()));
    }

    @Test
    @DisplayName("Значения сравниваются через equals, а не через toString")
    void comparesByEquals() {
        Task10CourseCode compact = Task10CourseCode.of("cs101");
        Task10CourseCode canonical = Task10CourseCode.of("CS-101");

        assertAll(
                () -> assertEquals(canonical, compact),
                () -> assertEquals(canonical.hashCode(), compact.hashCode()));
    }
}
