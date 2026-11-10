package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Задание 8. Course.completeHours (AAA)")
class Task08Test {

    @Test
    @DisplayName("completeHours(8) на активном курсе возвращает 8")
    void completeHoursAccumulates() {
        Task00Course course = new Task00Course(Task10CourseCode.of("CS-101"), "Java", 40, 30, 0, Task13CourseStatus.ACTIVE, null);

        int completed = course.completeHours(8);

        assertEquals(8, completed, "после completeHours(8) должно быть 8 часов");
    }
}
