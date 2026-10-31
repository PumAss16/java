package coursehub.practice8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task05Test {

    @Test
    @DisplayName("самый длинный курс находится через Optional")
    void findsLongestCourse() {
        Course c1 = new Course("C-1", "Alpha", 1, 90);
        Course c2 = new Course("C-2", "Beta", 1, 60);
        Course c3 = new Course("C-3", "Gamma", 1, 90);

        Optional<Course> longest = Task5LongestCourse.longest(List.of(c1, c2, c3));

        assertEquals(Optional.of(c3), longest);
        assertEquals("Gamma", Task5LongestCourse.titleOrFallback(
                List.of(c1, c2, c3), "no courses"));
    }

    @Test
    @DisplayName("пустой список даёт fallback")
    void emptyListGivesFallback() {
        assertTrue(Task5LongestCourse.longest(List.of()).isEmpty());
        assertEquals("no courses",
                Task5LongestCourse.titleOrFallback(List.of(), "no courses"));
    }
}
