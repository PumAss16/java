package collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task10Test {

    private static final class MutableKey {

        private String id;
        private String label;

        MutableKey(String id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof MutableKey key && id.equals(key.id) && label.equals(key.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, label);
        }
    }

    @Test
    @DisplayName("addView не меняет hashCode и не ломает поиск в HashSet и HashMap")
    void viewsDoNotBreakHashing() {
        Course course = new Course(CourseId.of("C-1"), CourseCode.of("CS-101"), "Java");
        Set<Course> set = new HashSet<>();
        Map<Course, Integer> map = new HashMap<>();
        set.add(course);
        map.put(course, 42);
        int before = course.hashCode();

        course.addView();

        assertAll(
                () -> assertEquals(1, course.views()),
                () -> assertEquals(before, course.hashCode()),
                () -> assertTrue(set.contains(course)),
                () -> assertEquals(42, map.get(course)),
                () -> assertTrue(map.containsKey(course))
        );
    }

    @Test
    @DisplayName("Изменение поля ключа ломает поиск в HashSet и HashMap")
    void mutatedKeyIsLostInHashSetAndHashMap() {
        MutableKey key = new MutableKey("1", "old");
        Set<MutableKey> set = new HashSet<>();
        Map<MutableKey, String> map = new HashMap<>();
        set.add(key);
        map.put(key, "value");

        key.label = "new";

        assertAll(
                () -> assertFalse(set.contains(key)),
                () -> assertNull(map.get(key)),
                () -> assertFalse(set.remove(key)),
                () -> assertFalse(map.remove(key, "value")),
                () -> assertEquals(1, set.size())
        );
    }

    @Test
    @DisplayName("После мутации ключа множество хранит два одинаковых ключа")
    void mutatedKeyCanBeAddedTwice() {
        MutableKey key = new MutableKey("1", "old");
        Set<MutableKey> set = new HashSet<>();
        set.add(key);

        key.label = "new";

        assertAll(
                () -> assertTrue(set.add(key)),
                () -> assertEquals(2, set.size())
        );
    }
}
