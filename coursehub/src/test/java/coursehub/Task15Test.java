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

/**
 * Задание 15. Полная граница ошибок CourseHub: use case импорта и зачисления.
 *
 * Четыре класса исходов различаются и не смешиваются:
 *   domain validation / баг — unchecked ValidationException, IllegalStateException, Error:
 *                             наружу из публичного слоя, в ответ не превращаются;
 *   ожидаемый отказ         — checked EnrollmentRejectedException / CourseImportException:
 *                             стабильный код CH-2001..CH-2006, CH-3001;
 *   инфраструктура          — unchecked InfrastructureException: код CH-4001, cause сохранён;
 *   отсутствие значения     — Optional/значение, а не исключение.
 *
 * Что ловится на каждом уровне:
 *   CourseParser   — ничего, бросает NumberFormatException;
 *   CourseImporter — только NumberFormatException / ValidationException (данные)
 *                    и IOException (чтение/закрытие);
 *   EnrollmentService — ничего: отказ отдаётся checked-исключением;
 *   CourseHub      — EnrollmentRejectedException, CourseImportException, InfrastructureException.
 * catch (Exception) запрещён: он превратил бы баг (NPE, сломанный инвариант) в «отказ
 * пользователю», спрятал бы дефект из мониторинга, смешал бы бизнес-отказ с поломкой
 * инфраструктуры и поймал бы InterruptedException без восстановления флага.
 */
@DisplayName("Задание 15. Граница ошибок CourseHub")
class Task15Test {

    /** Тестовый источник записей: строки, счётчики и флаги отказов. */
    static class Reader implements RecordReader {

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

    private CourseCatalog catalog;
    private EnrollmentService service;
    private CourseHub hub;

    @BeforeEach
    void setUp() {
        catalog = new CourseCatalog();
        service = new EnrollmentService(catalog);
        hub = new CourseHub(service, new CourseImporter());
    }

    private Course addCourse(String code, int capacity, int enrolled, CourseStatus status, CourseCode prerequisite) {
        Course course = new Course(CourseCode.of(code), "Course " + code, 40, capacity, enrolled, status, prerequisite);
        catalog.add(course);
        return course;
    }

    // ---------- зачисление: успех и ожидаемые отказы ----------

    @Test
    @DisplayName("Успешное зачисление: значение, занятое место, чистый журнал")
    void enrollOk() {
        addCourse("CS-101", 30, 0, CourseStatus.ACTIVE, null);
        service.addStudent(new Student("S-1"));

        CourseHub.Response<Course> response = hub.enroll("S-1", "cs-101");

        assertAll(
                () -> assertTrue(response.isOk()),
                () -> assertNull(response.code()),
                () -> assertEquals("CS-101", response.value().code().value()),
                () -> assertEquals(1, response.value().enrolled()),
                () -> assertTrue(hub.errors().isEmpty()));
    }

    @Test
    void enrollCourseNotFound() {
        service.addStudent(new Student("S-1"));

        assertEquals("CH-2001", hub.enroll("S-1", "CS-101").code());
    }

    @Test
    void enrollStudentNotFound() {
        addCourse("CS-101", 30, 0, CourseStatus.ACTIVE, null);

        assertEquals("CH-2002", hub.enroll("S-404", "CS-101").code());
    }

    @Test
    void enrollInactiveCourse() {
        addCourse("CS-101", 30, 0, CourseStatus.DRAFT, null);
        service.addStudent(new Student("S-1"));

        assertEquals("CH-2003", hub.enroll("S-1", "CS-101").code());
    }

    @Test
    void enrollFullCourse() {
        addCourse("CS-101", 1, 1, CourseStatus.ACTIVE, null);
        service.addStudent(new Student("S-1"));

        assertEquals("CH-2004", hub.enroll("S-1", "CS-101").code());
    }

    @Test
    void enrollWithoutPrerequisite() {
        addCourse("CS-202", 30, 0, CourseStatus.ACTIVE, CourseCode.of("MATH-101"));
        service.addStudent(new Student("S-1"));

        assertEquals("CH-2005", hub.enroll("S-1", "CS-202").code());
    }

    @Test
    void enrollTwice() {
        Course course = addCourse("CS-101", 30, 0, CourseStatus.ACTIVE, null);
        service.addStudent(new Student("S-1"));

        CourseHub.Response<Course> first = hub.enroll("S-1", "CS-101");
        CourseHub.Response<Course> second = hub.enroll("S-1", "CS-101");

        assertAll(
                () -> assertTrue(first.isOk()),
                () -> assertEquals("CH-2006", second.code()),
                () -> assertEquals(1, course.enrolled(), "дубликат не занял второе место"));
    }

    // ---------- domain validation и баги: наружу, не в ответ ----------

    @Test
    @DisplayName("Недопустимый код курса — ошибка программиста, а не отказ пользователю")
    void invalidCourseCodeIsAProgrammingError() {
        assertAll(
                () -> assertThrows(ValidationException.class, () -> hub.enroll("S-1", "!!!")),
                () -> assertTrue(hub.errors().isEmpty()));
    }

    @Test
    void blankStudentIdIsAProgrammingError() {
        addCourse("CS-101", 30, 0, CourseStatus.ACTIVE, null);

        assertThrows(ValidationException.class, () -> hub.enroll("   ", "CS-101"));
    }

    @Test
    void nullCourseCodeIsAProgrammingError() {
        assertThrows(ValidationException.class, () -> hub.enroll("S-1", null));
    }

    @Test
    @DisplayName("Инфраструктурный сбой: CH-4001, cause в журнале, детали не утекают")
    void infrastructureFailureReturnsStableCode() {
        CourseHub brokenHub = hubWithBrokenCatalog(
                new InfrastructureException("db down", new IOException("connection reset")));

        CourseHub.Response<Course> response = brokenHub.enroll("S-1", "CS-101");

        assertAll(
                () -> assertFalse(response.isOk()),
                () -> assertEquals("CH-4001", response.code()),
                () -> assertFalse(response.message().contains("reset")),
                () -> assertInstanceOf(InfrastructureException.class, brokenHub.errors().get(0)),
                () -> assertInstanceOf(IOException.class, brokenHub.errors().get(0).getCause()));
    }

    @Test
    @DisplayName("Баг (IllegalStateException) проходит насквозь: catch (Exception) запрещён")
    void programmingErrorIsNotConvertedToResponse() {
        CourseHub brokenHub = hubWithBrokenCatalog(new IllegalStateException("bug in catalog"));

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> brokenHub.enroll("S-1", "CS-101"));

        assertAll(
                () -> assertEquals("bug in catalog", e.getMessage()),
                () -> assertTrue(brokenHub.errors().isEmpty()));
    }

    @Test
    @DisplayName("Error тоже не ловится границей")
    void errorIsNotCaught() {
        CourseCatalog broken = new CourseCatalog() {
            @Override
            public Optional<Course> findCourse(CourseCode code) {
                throw new AssertionError("invariant broken");
            }
        };
        CourseHub brokenHub = new CourseHub(new EnrollmentService(broken), new CourseImporter());

        assertThrows(AssertionError.class, () -> brokenHub.enroll("S-1", "CS-101"));
    }

    @Test
    void rejectedEnrollmentDoesNotReserveSeat() {
        Course course = addCourse("CS-101", 1, 1, CourseStatus.ACTIVE, null);
        service.addStudent(new Student("S-1"));

        hub.enroll("S-1", "CS-101");

        assertEquals(1, course.enrolled());
    }

    @Test
    void expectedRefusalIsJournaledWithItsCause() {
        addCourse("CS-101", 30, 0, CourseStatus.ARCHIVED, null);
        service.addStudent(new Student("S-1"));

        hub.enroll("S-1", "CS-101");

        Throwable journaled = hub.errors().get(0);

        assertAll(
                () -> assertInstanceOf(EnrollmentRejectedException.class, journaled),
                () -> assertEquals(RejectionReason.COURSE_NOT_ACTIVE,
                        ((EnrollmentRejectedException) journaled).reason()));
    }

    @Test
    void expectedRefusalsNeverThrow() {
        addCourse("CS-101", 1, 1, CourseStatus.ACTIVE, null);
        service.addStudent(new Student("S-1"));

        assertAll(
                () -> assertDoesNotThrow(() -> hub.enroll("S-1", "CS-101")),
                () -> assertDoesNotThrow(() -> hub.enroll("S-404", "CS-101")),
                () -> assertDoesNotThrow(() -> hub.enroll("S-1", "XX-999")),
                () -> assertDoesNotThrow(() -> hub.importCourses(new Reader(List.of("bad"), false, false))));
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(RejectionReason.class)
    @DisplayName("Коды ожидаемых отказов стабильны")
    void rejectionCodesAreStable(RejectionReason reason) {
        assertTrue(reason.code().matches("CH-2\\d{3}"), reason.name());
    }

    // ---------- импорт ----------

    @Test
    void importOk() {
        Reader reader = new Reader(List.of(GOOD, "MATH-101;Math;25;60"), false, false);

        CourseHub.Response<List<Course>> response = hub.importCourses(reader);

        assertAll(
                () -> assertTrue(response.isOk()),
                () -> assertEquals(2, response.value().size()),
                () -> assertTrue(reader.closed));
    }

    @Test
    @DisplayName("Malformed record: CH-3001 и номер записи в ответе")
    void importMalformedRecordReturnsRecordNumber() {
        Reader reader = new Reader(List.of(GOOD, BAD), false, false);

        CourseHub.Response<List<Course>> response = hub.importCourses(reader);

        assertAll(
                () -> assertEquals("CH-3001", response.code()),
                () -> assertTrue(response.message().contains("2"), response.message()));
    }

    @Test
    void importKeepsCauseChain() {
        hub.importCourses(new Reader(List.of(GOOD, BAD), false, false));

        Throwable journaled = hub.errors().get(0);

        assertAll(
                () -> assertInstanceOf(CourseImportException.class, journaled),
                () -> assertEquals(2, ((CourseImportException) journaled).recordNumber()),
                () -> assertInstanceOf(NumberFormatException.class, journaled.getCause()));
    }

    @Test
    void importReadFailureReturnsInfrastructureCode() {
        CourseHub.Response<List<Course>> response = hub.importCourses(new Reader(List.of(), true, false));

        assertAll(
                () -> assertEquals("CH-4001", response.code()),
                () -> assertInstanceOf(InfrastructureException.class, hub.errors().get(0)),
                () -> assertInstanceOf(IOException.class, hub.errors().get(0).getCause()));
    }

    @Test
    void importCloseFailureDoesNotPublishResult() {
        Reader reader = new Reader(List.of(GOOD), false, true);

        CourseHub.Response<List<Course>> response = hub.importCourses(reader);

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
                () -> assertInstanceOf(CourseImportException.class, journaled),
                () -> assertEquals(1, journaled.getSuppressed().length),
                () -> assertInstanceOf(IOException.class, journaled.getSuppressed()[0]));
    }

    @Test
    void importHasNoPartialResult() {
        CourseHub.Response<List<Course>> response = hub.importCourses(new Reader(List.of(GOOD, BAD), false, false));

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
        CourseHub.Response<List<Course>> response = hub.importCourses(new Reader(List.of(), true, false));

        assertAll(
                () -> assertFalse(response.message().contains("IOException")),
                () -> assertFalse(response.message().contains("disk error")));
    }

    /** Публичный слой на курсе, чей каталог падает заданной ошибкой. */
    private CourseHub hubWithBrokenCatalog(RuntimeException failure) {
        CourseCatalog broken = new CourseCatalog() {
            @Override
            public Optional<Course> findCourse(CourseCode code) {
                throw failure;
            }
        };
        return new CourseHub(new EnrollmentService(broken), new CourseImporter());
    }
}
