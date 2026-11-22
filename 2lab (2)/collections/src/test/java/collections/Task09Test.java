package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task09Test {

    private static final Course JAVA =
            new Course(CourseId.of("C-1"), CourseCode.of("java"), "Java basics", Tag.of("backend"));

    private static final Course GO =
            new Course(CourseId.of("C-2"), CourseCode.of("go"), "Go basics", Tag.of("backend"));

    @Test
    @DisplayName("copy копирует Integer в List<Number>")
    void copyIntegersToNumbers() {
        List<Integer> src = new ArrayList<>(List.of(1, 2, 3));
        List<Number> dst = new ArrayList<>();
        Generics.copy(src, dst);
        assertEquals(List.of(1, 2, 3), dst);
        assertEquals(3, dst.size());
    }

    @Test
    @DisplayName("copy копирует String в List<Object>")
    void copyStringsToObjects() {
        List<String> src = List.of("a", "b", "c");
        List<Object> dst = new ArrayList<>();
        Generics.copy(src, dst);
        assertEquals(List.of("a", "b", "c"), dst);
    }

    @Test
    @DisplayName("copy сохраняет порядок и не алиасит источник")
    void copyKeepsOrderAndIsNotAliased() {
        List<String> src = new ArrayList<>(List.of("a", "b", "c"));
        List<Object> dst = new ArrayList<>();
        Generics.copy(src, dst);
        src.set(0, "z");
        src.add("d");
        assertEquals(List.of("a", "b", "c"), dst);
        assertEquals(List.of("z", "b", "c", "d"), src);
    }

    @Test
    @DisplayName("из List<? extends Course> можно читать")
    void readFromExtendsCourse() {
        List<Course> courses = List.of(JAVA, GO);
        assertEquals("Java basics", firstTitle(courses));
        assertEquals("Go basics", firstTitle(new ArrayList<>(List.of(GO))));
    }

    @Test
    @DisplayName("в List<? super Course> можно писать")
    void writeToSuperCourse() {
        List<Object> sink = new ArrayList<>();
        Generics.copy(List.of(JAVA), sink);
        sink.add(GO);
        assertEquals(List.of(JAVA, GO), sink);
        assertTrue(sink.contains(JAVA));
    }

    private static String firstTitle(List<? extends Course> courses) {
        return courses.get(0).title();
    }
}
