package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task03Test {

    private static Course course(String id, String code, String title) {
        return new Course(CourseId.of(id), CourseCode.of(code), title, Tag.of("java"));
    }

    @Test
    @DisplayName("Индекс ищет курсы по id")
    void findsCoursesById() {
        CourseIndex index = new CourseIndex();
        Course algorithms = course("cs-101", "CS-101", "Algorithms");
        Course math = course("ma-200", "MA-200", "Math");
        assertTrue(index.add(algorithms));
        assertTrue(index.add(math));
        assertEquals(2, index.size());
        assertTrue(index.has(CourseId.of("cs-101")));
        assertFalse(index.has(CourseId.of("ph-300")));
        assertSame(algorithms, index.find(CourseId.of("cs-101")).orElseThrow());
        assertTrue(index.find(CourseId.of("ph-300")).isEmpty());
    }

    @Test
    @DisplayName("Повторный id не перезаписывает курс и приводит к исключению")
    void duplicateIdIsRejectedWithoutOverwrite() {
        CourseIndex index = new CourseIndex();
        Course original = course("cs-101", "CS-101", "Algorithms");
        Course duplicate = course("cs-101", "CS-101", "Algorithms again");
        assertTrue(index.add(original));
        assertFalse(index.add(duplicate));
        assertEquals(1, index.size());
        assertEquals("Algorithms", index.find(CourseId.of("cs-101")).orElseThrow().title());

        DuplicateIdException error = assertThrows(DuplicateIdException.class, () -> index.addOrThrow(duplicate));
        assertEquals(CourseId.of("cs-101"), error.id());
        assertEquals("duplicate course id: cs-101", error.getMessage());
    }

    @Test
    @DisplayName("Четыре варианта вставки дают одинаковый результат")
    void allInsertVariantsBehaveTheSame() {
        CourseIndex index = new CourseIndex();
        assertTrue(index.insertViaGet(course("cs-101", "CS-101", "Algorithms")));
        assertTrue(index.insertViaContainsKey(course("ma-200", "MA-200", "Math")));
        assertTrue(index.insertViaPutIfAbsent(course("ph-300", "PH-300", "Physics")));
        assertTrue(index.insertViaComputeIfAbsent(course("ch-400", "CH-400", "Chemistry")));
        assertEquals(4, index.size());

        assertFalse(index.insertViaGet(course("cs-101", "CS-101", "Algorithms again")));
        assertFalse(index.insertViaContainsKey(course("ma-200", "MA-200", "Math again")));
        assertFalse(index.insertViaPutIfAbsent(course("ph-300", "PH-300", "Physics again")));
        assertFalse(index.insertViaComputeIfAbsent(course("ch-400", "CH-400", "Chemistry again")));
        assertEquals(4, index.size());

        assertFalse(index.insertViaPutIfAbsent(course("cs-101", "CS-101", "Algorithms once more")));
        assertFalse(index.insertViaComputeIfAbsent(course("cs-101", "CS-101", "Algorithms once more")));
        assertFalse(index.insertViaGet(course("ma-200", "MA-200", "Math once more")));
        assertFalse(index.insertViaContainsKey(course("ph-300", "PH-300", "Physics once more")));
        assertEquals(4, index.size());
    }

    @Test
    @DisplayName("findOrCompute вызывает фабрику ровно один раз")
    void findOrComputeCallsFactoryOnce() {
        CourseIndex index = new CourseIndex();
        AtomicInteger counter = new AtomicInteger();
        CourseId id = CourseId.of("cs-101");
        Course first = index.findOrCompute(id, () -> {
            counter.incrementAndGet();
            return course("cs-101", "CS-101", "Algorithms");
        });
        Course second = index.findOrCompute(id, () -> {
            counter.incrementAndGet();
            return course("cs-101", "CS-101", "Algorithms");
        });
        assertEquals(1, counter.get());
        assertSame(first, second);
        assertEquals(1, index.size());
    }

    @Test
    @DisplayName("replace возвращает предыдущий курс")
    void replaceReturnsPreviousCourse() {
        CourseIndex index = new CourseIndex();
        Course original = course("cs-101", "CS-101", "Algorithms");
        Course updated = course("cs-101", "CS-101", "Algorithms 2");
        assertNull(index.replace(original));
        assertSame(original, index.replace(updated));
        assertEquals("Algorithms 2", index.find(CourseId.of("cs-101")).orElseThrow().title());
        assertEquals(1, index.size());
    }
}
