package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Задание 8. Первый тест по AAA: одна причина падения, имя описывает поведение. */
@DisplayName("Задание 8. Course.completeHours (AAA)")
class Task08Test {

    @Test
    @DisplayName("completeHours(8) на активном курсе возвращает 8")
    void completeHoursAccumulates() {
        // Arrange
        Course course = new Course(CourseCode.of("CS-101"), "Java", 40, 30, 0, CourseStatus.ACTIVE, null);

        // Act
        int completed = course.completeHours(8);

        // Assert — единственная причина падения
        assertEquals(8, completed, "после completeHours(8) должно быть 8 часов");
    }
}
