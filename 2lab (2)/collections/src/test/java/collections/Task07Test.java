package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task07Test {

    private static final Course JAVA =
            new Course(CourseId.of("C-1"), CourseCode.of("java"), "Java basics", Tag.of("backend"));

    private static final Course GO =
            new Course(CourseId.of("C-2"), CourseCode.of("go"), "Go basics", Tag.of("backend"));

    private static final Course JAVA_ADVANCED =
            new Course(CourseId.of("C-3"), CourseCode.of("java"), "Java advanced", Tag.of("advanced"));

    @Test
    @DisplayName("firstOrThrow возвращает первый элемент")
    void firstOrThrowReturnsFirst() {
        assertSame(JAVA, Generics.firstOrThrow(List.of(JAVA, GO)));
        assertEquals(1, Generics.firstOrThrow(List.of(1, 2, 3)));
    }

    @Test
    @DisplayName("firstOrThrow на пустом списке бросает NoSuchElementException")
    void firstOrThrowOnEmptyList() {
        NoSuchElementException empty =
                assertThrows(NoSuchElementException.class, () -> Generics.firstOrThrow(List.<Course>of()));
        assertEquals("empty list", empty.getMessage());
        assertThrows(NoSuchElementException.class, () -> Generics.firstOrThrow(null));
    }

    @Test
    @DisplayName("swap меняет местами середину списка")
    void swapSwapsMiddle() {
        List<String> items = new ArrayList<>(List.of("a", "b", "c", "d"));
        Generics.swap(items, 1, 2);
        assertEquals(List.of("a", "c", "b", "d"), items);
    }

    @Test
    @DisplayName("swap меняет местами края списка")
    void swapSwapsEdges() {
        List<Integer> items = new ArrayList<>(List.of(1, 2, 3));
        Generics.swap(items, 0, 2);
        assertEquals(List.of(3, 2, 1), items);
        Generics.swap(items, 1, 1);
        assertEquals(List.of(3, 2, 1), items);
    }

    @Test
    @DisplayName("swap на невалидных индексах бросает IndexOutOfBoundsException")
    void swapRejectsInvalidIndexes() {
        List<Integer> items = new ArrayList<>(List.of(1, 2, 3));
        IndexOutOfBoundsException low =
                assertThrows(IndexOutOfBoundsException.class, () -> Generics.swap(items, -1, 1));
        assertTrue(low.getMessage().contains("-1"));
        IndexOutOfBoundsException high =
                assertThrows(IndexOutOfBoundsException.class, () -> Generics.swap(items, 0, 3));
        assertTrue(high.getMessage().contains("3"));
        assertEquals(List.of(1, 2, 3), items);
    }

    @Test
    @DisplayName("indexBy индексирует курсы по коду с порядком вставки")
    void indexByCourseCodeKeepsOrder() {
        Map<CourseCode, Course> byCode = Generics.indexBy(List.of(JAVA, GO), Course::code);
        assertEquals(2, byCode.size());
        assertSame(JAVA, byCode.get(CourseCode.of("java")));
        assertSame(GO, byCode.get(CourseCode.of("go")));
        assertEquals(List.of(CourseCode.of("java"), CourseCode.of("go")), new ArrayList<>(byCode.keySet()));
    }

    @Test
    @DisplayName("indexBy на повторном ключе бросает IllegalStateException")
    void indexByRejectsDuplicateKey() {
        IllegalStateException duplicate = assertThrows(IllegalStateException.class,
                () -> Generics.indexBy(List.of(JAVA, JAVA_ADVANCED), Course::code));
        assertTrue(duplicate.getMessage().contains("duplicate key"));
        assertTrue(duplicate.getMessage().contains("JAVA"));
    }

    @Test
    @DisplayName("indexBy с merge оставляет первый курс")
    void indexByMergeKeepsFirst() {
        Map<CourseCode, Course> byCode =
                Generics.<Course, CourseCode>indexBy(List.of(JAVA, JAVA_ADVANCED), Course::code, (a, b) -> a);
        assertEquals(1, byCode.size());
        assertSame(JAVA, byCode.get(CourseCode.of("java")));
    }

    @Test
    @DisplayName("indexBy с merge оставляет последний курс")
    void indexByMergeKeepsLast() {
        Map<CourseCode, Course> byCode =
                Generics.<Course, CourseCode>indexBy(List.of(JAVA, JAVA_ADVANCED), Course::code, (a, b) -> b);
        assertEquals(1, byCode.size());
        assertSame(JAVA_ADVANCED, byCode.get(CourseCode.of("java")));
    }
}
