package coursehub.practice8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task07Test {

    private static final Course C1 = new Course("C-1", "Java", 1, 60);
    private static final Course C2 = new Course("C-2", "SQL", 2, 90);
    private static final Course C3 = new Course("C-3", "Go", 1, 30);

    @Test
    @DisplayName("один проход считает студентов, rates, top-3, уровни и аномалии")
    void onePassBuildsWholeReport() {
        EnrollmentReport report = Task7EnrollmentReporter.report(
                events().stream(), List.of(C1, C2, C3));

        assertEquals(13, report.eventsRead());
        assertEquals(List.of("S-1", "S-2", "S-3", "S-4", "S-5"),
                report.uniqueStudents());
        assertEquals(List.of("C-1", "C-2", "C-3"),
                List.copyOf(report.completionRateByCourse().keySet()));
        assertEquals(1.0, report.completionRateByCourse().get("C-1"), 1e-9);
        assertEquals(1.0, report.completionRateByCourse().get("C-2"), 1e-9);
        assertEquals(0.0, report.completionRateByCourse().get("C-3"), 1e-9);
        assertEquals(List.of("C-1", "C-2", "C-3"),
                report.top3().stream().map(CourseCompletion::courseCode).toList());
        assertEquals(3L, report.enrollmentsByLevel().get(1));
        assertEquals(1L, report.enrollmentsByLevel().get(2));
        assertEquals(5, report.anomalyCount());
        assertEquals(List.of(
                "completion without enrollment",
                "duplicate enrollment",
                "duplicate completion",
                "blank student id",
                "unknown course"),
                report.anomalies().stream().map(Anomaly::reason).toList());
    }

    @Test
    @DisplayName("отчёт детерминирован при одинаковом потоке событий")
    void reportIsDeterministic() {
        EnrollmentReport first = Task7EnrollmentReporter.report(
                events().stream(), List.of(C1, C2, C3));
        EnrollmentReport second = Task7EnrollmentReporter.report(
                events().stream(), List.of(C1, C2, C3));

        assertEquals(first, second);
    }

    @Test
    @DisplayName("список аномалий ограничен, но счётчик полный")
    void anomaliesAreBounded() {
        Stream<EnrollmentEvent> events = IntStream.range(0, 105)
                .mapToObj(index -> new EnrollmentEvent(" ", "C-1", EventType.ENROLLED));

        EnrollmentReport report = Task7EnrollmentReporter.report(events, List.of(C1));

        assertEquals(105, report.eventsRead());
        assertEquals(105, report.anomalyCount());
        assertEquals(100, report.anomalies().size());
        assertTrue(report.uniqueStudents().isEmpty());
        assertTrue(report.top3().isEmpty());
        assertTrue(report.enrollmentsByLevel().isEmpty());
        assertEquals(0.0, report.completionRateByCourse().get("C-1"), 1e-9);
    }

    @Test
    @DisplayName("агрегаты отчёта неизменяемые")
    void reportAggregatesAreImmutable() {
        EnrollmentReport report = Task7EnrollmentReporter.report(
                events().stream(), List.of(C1, C2, C3));

        assertThrows(UnsupportedOperationException.class,
                () -> report.uniqueStudents().add("S-9"));
        assertThrows(UnsupportedOperationException.class,
                () -> report.completionRateByCourse().put("C-9", 0.0));
    }

    private static List<EnrollmentEvent> events() {
        return List.of(
                new EnrollmentEvent("S-1", "C-1", EventType.ENROLLED),
                new EnrollmentEvent("S-2", "C-1", EventType.ENROLLED),
                new EnrollmentEvent("S-1", "C-1", EventType.COMPLETED),
                new EnrollmentEvent("S-2", "C-1", EventType.COMPLETED),
                new EnrollmentEvent("S-1", "C-2", EventType.ENROLLED),
                new EnrollmentEvent("S-1", "C-2", EventType.COMPLETED),
                new EnrollmentEvent("S-3", "C-3", EventType.ENROLLED),
                new EnrollmentEvent("S-4", "C-1", EventType.COMPLETED),
                new EnrollmentEvent("S-1", "C-1", EventType.ENROLLED),
                new EnrollmentEvent("S-1", "C-1", EventType.COMPLETED),
                new EnrollmentEvent(" ", "C-1", EventType.ENROLLED),
                new EnrollmentEvent("S-5", "C-9", EventType.ENROLLED),
                new EnrollmentEvent("S-5", "C-1", EventType.CANCELLED));
    }
}
