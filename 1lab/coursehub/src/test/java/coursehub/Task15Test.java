package coursehub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Задание 15. Граница ошибок CourseHub")
class Task15Test {

    static class Reader implements Task14RecordReader {

        private final List<String> lines;
        private final boolean failOnRead;
        private final boolean failOnClose;
        private int index;
        private boolean closed;

        Reader(List<String> lines, boolean failOnRead, boolean failOnClose) {
            this.lines = lines;
            this.failOnRead = failOnRead;
            this.failOnClose = failOnClose;
        }

        @Override
        public String readRecord() throws IOException {
            if (failOnRead) {
                throw new IOException("disk error");
            }
            return index < lines.size() ? lines.get(index++) : null;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if (failOnClose) {
                throw new IOException("close failed");
            }
        }
    }

    private static final String GOOD = "CS-101;Java;30;40";
    private static final String BAD = "CS-102;Broken;30;oops";

    private Task07CourseCatalog catalog;
    private Task07EnrollmentService service;
    private Task15CourseHub hub;

    @BeforeEach
    void setUp() {
        catalog = new Task07CourseCatalog();
        service = new Task07EnrollmentService(catalog);
        hub = new Task15CourseHub(service, new Task06CourseImporter());
    }

    private Task00Course addCourse(String code, int capacity, int enrolled, Task13CourseStatus status, Task10CourseCode prerequisite) {
        Task00Course course = new Task00Course(Task10CourseCode.of(code), "Course " + code, 40, capacity, enrolled, status, prerequisite);
        catalog.add(course);
        return course;
    }

    @Test
    @DisplayName("Успешное зачисление: значение, занятое место, чистый журнал")
    void enrollOk() {
        addCourse("CS-101", 30, 0, Task13CourseStatus.ACTIVE, null);
        service.addStudent(new Task00Student("S-1"));

        Task15CourseHub.Response<Task00Course> response = hub.enroll("S-1", "cs-101");

        assertAll(
                () -> assertTrue(response.isOk()),
                () -> assertNull(response.code()),
                () -> assertEquals("CS-101", response.value().code().value()),
                () -> assertEquals(1, response.value().enrolled()),
                () -> assertTrue(hub.errors().isEmpty()));
    }

    @Test
    void enrollCourseNotFound() {
        service.addStudent(new Task00Student("S-1"));

        assertEquals("CH-2001", hub.enroll("S-1", "CS-101").code());
    }

    @Test
    void enrollStudentNotFound() {
        addCourse("CS-101", 30, 0, Task13CourseStatus.ACTIVE, null);

        assertEquals("CH-2002", hub.enroll("S-404", "CS-101").code());
    }

    @Test
    void enrollInactiveCourse() {
        addCourse("CS-101", 30, 0, Task13CourseStatus.DRAFT, null);
        service.addStudent(new Task00Student("S-1"));

        assertEquals("CH-2003", hub.enroll("S-1", "CS-101").code());
    }

    @Test
    void enrollFullCourse() {
        addCourse("CS-101", 1, 1, Task13CourseStatus.ACTIVE, null);
        service.addStudent(new Task00Student("S-1"));

        assertEquals("CH-2004", hub.enroll("S-1", "CS-101").code());
    }

    @Test
    void enrollWithoutPrerequisite() {
        addCourse("CS-202", 30, 0, Task13CourseStatus.ACTIVE, Task10CourseCode.of("MATH-101"));
        service.addStudent(new Task00Student("S-1"));

        assertEquals("CH-2005", hub.enroll("S-1", "CS-202").code());
    }

    @Test
    void enrollTwice() {
        Task00Course course = addCourse("CS-101", 30, 0, Task13CourseStatus.ACTIVE, null);
        service.addStudent(new Task00Student("S-1"));

        Task15CourseHub.Response<Task00Course> first = hub.enroll("S-1", "CS-101");
        Task15CourseHub.Response<Task00Course> second = hub.enroll("S-1", "CS-101");

        assertAll(
                () -> assertTrue(first.isOk()),
                () -> assertEquals("CH-2006", second.code()),
                () -> assertEquals(1, course.enrolled(), "дубликат не занял второе место"));
    }

    @Test
    @DisplayName("Недопустимый код курса — ошибка программиста, а не отказ пользователю")
    void invalidCourseCodeIsAProgrammingError() {
        assertAll(
                () -> assertThrows(Task02ValidationException.class, () -> hub.enroll("S-1", "!!!")),
                () -> assertTrue(hub.errors().isEmpty()));
    }

    @Test
    void blankStudentIdIsAProgrammingError() {
        addCourse("CS-101", 30, 0, Task13CourseStatus.ACTIVE, null);

        assertThrows(Task02ValidationException.class, () -> hub.enroll("   ", "CS-101"));
    }

    @Test
    void nullCourseCodeIsAProgrammingError() {
        assertThrows(Task02ValidationException.class, () -> hub.enroll("S-1", null));
    }

    @Test
    @DisplayName("Инфраструктурный сбой: CH-4001, cause в журнале, детали не утекают")
    void infrastructureFailureReturnsStableCode() {
        Task15CourseHub brokenHub = hubWithBrokenCatalog(
                new Task15InfrastructureException("db down", new IOException("connection reset")));

        Task15CourseHub.Response<Task00Course> response = brokenHub.enroll("S-1", "CS-101");

        assertAll(
                () -> assertFalse(response.isOk()),
                () -> assertEquals("CH-4001", response.code()),
                () -> assertFalse(response.message().contains("reset")),
                () -> assertInstanceOf(Task15InfrastructureException.class, brokenHub.errors().get(0)),
                () -> assertInstanceOf(IOException.class, brokenHub.errors().get(0).getCause()));
    }

    @Test
    @DisplayName("Баг (IllegalStateException) проходит насквозь: catch (Exception) запрещён")
    void programmingErrorIsNotConvertedToResponse() {
        Task15CourseHub brokenHub = hubWithBrokenCatalog(new IllegalStateException("bug in catalog"));

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> brokenHub.enroll("S-1", "CS-101"));

        assertAll(
                () -> assertEquals("bug in catalog", e.getMessage()),
                () -> assertTrue(brokenHub.errors().isEmpty()));
    }

    @Test
    @DisplayName("Error тоже не ловится границей")
    void errorIsNotCaught() {
        Task07CourseCatalog broken = new Task07CourseCatalog() {
            @Override
            public Optional<Task00Course> findCourse(Task10CourseCode code) {
                throw new AssertionError("invariant broken");
            }
        };
        Task15CourseHub brokenHub = new Task15CourseHub(new Task07EnrollmentService(broken), new Task06CourseImporter());

        assertThrows(AssertionError.class, () -> brokenHub.enroll("S-1", "CS-101"));
    }

    @Test
    void rejectedEnrollmentDoesNotReserveSeat() {
        Task00Course course = addCourse("CS-101", 1, 1, Task13CourseStatus.ACTIVE, null);
        service.addStudent(new Task00Student("S-1"));

        hub.enroll("S-1", "CS-101");

        assertEquals(1, course.enrolled());
    }

    @Test
    void expectedRefusalIsJournaledWithItsCause() {
        addCourse("CS-101", 30, 0, Task13CourseStatus.ARCHIVED, null);
        service.addStudent(new Task00Student("S-1"));

        hub.enroll("S-1", "CS-101");

        Throwable journaled = hub.errors().get(0);

        assertAll(
                () -> assertInstanceOf(Task03EnrollmentRejectedException.class, journaled),
                () -> assertEquals(Task03RejectionReason.COURSE_NOT_ACTIVE,
                        ((Task03EnrollmentRejectedException) journaled).reason()));
    }

    @Test
    void expectedRefusalsNeverThrow() {
        addCourse("CS-101", 1, 1, Task13CourseStatus.ACTIVE, null);
        service.addStudent(new Task00Student("S-1"));

        assertAll(
                () -> assertDoesNotThrow(() -> hub.enroll("S-1", "CS-101")),
                () -> assertDoesNotThrow(() -> hub.enroll("S-404", "CS-101")),
                () -> assertDoesNotThrow(() -> hub.enroll("S-1", "XX-999")),
                () -> assertDoesNotThrow(() -> hub.importCourses(new Reader(List.of("bad"), false, false))));
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Task03RejectionReason.class)
    @DisplayName("Коды ожидаемых отказов стабильны")
    void rejectionCodesAreStable(Task03RejectionReason reason) {
        assertTrue(reason.code().matches("CH-2\\d{3}"), reason.name());
    }

    @Test
    void importOk() {
        Reader reader = new Reader(List.of(GOOD, "MATH-101;Math;25;60"), false, false);

        Task15CourseHub.Response<List<Task00Course>> response = hub.importCourses(reader);

        assertAll(
                () -> assertTrue(response.isOk()),
                () -> assertEquals(2, response.value().size()),
                () -> assertTrue(reader.closed));
    }

    @Test
    @DisplayName("Malformed record: CH-3001 и номер записи в ответе")
    void importMalformedRecordReturnsRecordNumber() {
        Reader reader = new Reader(List.of(GOOD, BAD), false, false);

        Task15CourseHub.Response<List<Task00Course>> response = hub.importCourses(reader);

        assertAll(
                () -> assertEquals("CH-3001", response.code()),
                () -> assertTrue(response.message().contains("2"), response.message()));
    }

    @Test
    void importKeepsCauseChain() {
        hub.importCourses(new Reader(List.of(GOOD, BAD), false, false));

        Throwable journaled = hub.errors().get(0);

        assertAll(
                () -> assertInstanceOf(Task06CourseImportException.class, journaled),
                () -> assertEquals(2, ((Task06CourseImportException) journaled).recordNumber()),
                () -> assertInstanceOf(NumberFormatException.class, journaled.getCause()));
    }

    @Test
    void importReadFailureReturnsInfrastructureCode() {
        Task15CourseHub.Response<List<Task00Course>> response = hub.importCourses(new Reader(List.of(), true, false));

        assertAll(
                () -> assertEquals("CH-4001", response.code()),
                () -> assertInstanceOf(Task15InfrastructureException.class, hub.errors().get(0)),
                () -> assertInstanceOf(IOException.class, hub.errors().get(0).getCause()));
    }

    @Test
    void importCloseFailureDoesNotPublishResult() {
        Reader reader = new Reader(List.of(GOOD), false, true);

        Task15CourseHub.Response<List<Task00Course>> response = hub.importCourses(reader);

        assertAll(
                () -> assertEquals("CH-4001", response.code()),
                () -> assertNull(response.value(), "частичного результата нет"),
                () -> assertTrue(reader.closed));
    }

    @Test
    @DisplayName("Два отказа импорта: основной — ошибка данных, suppressed — ошибка закрытия")
    void importMalformedAndCloseFailureKeepsBoth() {
        hub.importCourses(new Reader(List.of(GOOD, BAD), false, true));

        Throwable journaled = hub.errors().get(0);

        assertAll(
                () -> assertInstanceOf(Task06CourseImportException.class, journaled),
                () -> assertEquals(1, journaled.getSuppressed().length),
                () -> assertInstanceOf(IOException.class, journaled.getSuppressed()[0]));
    }

    @Test
    void importHasNoPartialResult() {
        Task15CourseHub.Response<List<Task00Course>> response = hub.importCourses(new Reader(List.of(GOOD, BAD), false, false));

        assertAll(
                () -> assertFalse(response.isOk()),
                () -> assertNull(response.value()));
    }

    @Test
    void readerIsClosedInBothOutcomes() {
        Reader good = new Reader(List.of(GOOD), false, false);
        Reader bad = new Reader(List.of(BAD), false, false);

        hub.importCourses(good);
        hub.importCourses(bad);

        assertAll(
                () -> assertTrue(good.closed),
                () -> assertTrue(bad.closed));
    }

    @Test
    void publicMessageDoesNotLeakInternals() {
        Task15CourseHub.Response<List<Task00Course>> response = hub.importCourses(new Reader(List.of(), true, false));

        assertAll(
                () -> assertFalse(response.message().contains("IOException")),
                () -> assertFalse(response.message().contains("disk error")));
    }

    private Task15CourseHub hubWithBrokenCatalog(RuntimeException failure) {
        Task07CourseCatalog broken = new Task07CourseCatalog() {
            @Override
            public Optional<Task00Course> findCourse(Task10CourseCode code) {
                throw failure;
            }
        };
        return new Task15CourseHub(new Task07EnrollmentService(broken), new Task06CourseImporter());
    }
}
