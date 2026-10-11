package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task06Test {

    private static final Course COURSE =
            new Course(CourseId.of("C-1"), CourseCode.of("java"), "Java basics", Tag.of("backend"));

    private static final Course FALLBACK =
            new Course(CourseId.of("C-9"), CourseCode.of("go"), "Go basics", Tag.of("backend"));

    @Test
    @DisplayName("ok хранит курс")
    void okKeepsCourse() {
        Result<Course> result = Result.ok(COURSE);
        assertTrue(result.isOk());
        assertSame(COURSE, result.value());
        assertSame(COURSE, result.orElse(FALLBACK));
    }

    @Test
    @DisplayName("ok хранит список идентификаторов")
    void okKeepsListOfCourseIds() {
        List<CourseId> ids = List.of(CourseId.of("C-1"), CourseId.of("C-2"));
        Result<List<CourseId>> result = Result.ok(ids);
        assertTrue(result.isOk());
        assertEquals(ids, result.value());
        assertEquals(ids, result.orElse(List.of(CourseId.of("C-0"))));
    }

    @Test
    @DisplayName("orElse возвращает запасное значение при ошибке")
    void orElseReturnsFallback() {
        Result<Course> failure = Result.fail("boom");
        assertFalse(failure.isOk());
        assertSame(FALLBACK, failure.orElse(FALLBACK));
    }

    @Test
    @DisplayName("map переводит курс в его заголовок")
    void mapCourseToTitle() {
        Result<String> mapped = Result.ok(COURSE).map(Course::title);
        assertTrue(mapped.isOk());
        assertEquals("Java basics", mapped.value());
    }

    @Test
    @DisplayName("ok(null) запрещён")
    void okRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> Result.ok(null));
    }

    @Test
    @DisplayName("fail с пустой строкой запрещён")
    void failRejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> Result.fail(" "));
        assertThrows(IllegalArgumentException.class, () -> Result.fail(null));
    }

    @Test
    @DisplayName("value на ошибке и error на успехе бросают IllegalStateException")
    void mismatchedAccessorsThrow() {
        Result<Course> failure = Result.fail("boom");
        assertEquals("boom", failure.error());
        assertThrows(IllegalStateException.class, failure::value);
        Result<Course> success = Result.ok(COURSE);
        assertThrows(IllegalStateException.class, success::error);
    }

    @Test
    @DisplayName("обобщённый unwrap работает и с курсом, и со списком")
    void unwrapWorksForDifferentTypeArguments() {
        assertSame(COURSE, unwrap(Result.ok(COURSE)));
        List<CourseId> ids = List.of(CourseId.of("C-1"));
        assertEquals(ids, unwrap(Result.ok(ids)));
    }

    @Test
    @DisplayName("raw-тип теряет защиту типов")
    @SuppressWarnings({"rawtypes", "unchecked"})
    void rawTypeLosesTypeSafety() {
        Result raw = Result.ok(COURSE);
        Result<CourseId> forged = raw;
        assertThrows(ClassCastException.class, () -> {
            CourseId bad = forged.value();
            assertNotNull(bad);
        });
        Result<CourseId> typed = Result.ok(CourseId.of("C-1"));
        assertEquals(CourseId.of("C-1"), typed.value());
    }

    @Test
    @DisplayName("map на ошибке не вызывает маппер")
    void mapOnFailureSkipsMapper() {
        AtomicInteger calls = new AtomicInteger();
        Result<String> mapped = Result.<Course>fail("boom").map(course -> {
            calls.incrementAndGet();
            return course.title();
        });
        assertFalse(mapped.isOk());
        assertEquals("boom", mapped.error());
        assertEquals(0, calls.get());
    }

    private static <T> T unwrap(Result<T> result) {
        return result.value();
    }
}
