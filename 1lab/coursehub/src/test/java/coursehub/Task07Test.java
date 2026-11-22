package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Задание 7. Значение или исключение")
class Task07Test {

    private Task00Course course(String code, int capacity, int enrolled) {
        return new Task00Course(Task10CourseCode.of(code), "Course " + code, 40, capacity, enrolled, Task13CourseStatus.ACTIVE, null);
    }

    @Test
    @DisplayName("findCourse: нет курса — Optional.empty, исключения нет")
    void findCourseReturnsOptional() {
        Task07CourseCatalog catalog = new Task07CourseCatalog();

        Optional<Task00Course> found = catalog.findCourse(Task10CourseCode.of("CS-101"));

        assertAll(
                () -> assertEquals(Optional.empty(), found),
                () -> assertNotNull(found));
    }

    @Test
    @DisplayName("Контраст 1: один факт — Optional в поиске и checked-отказ в enroll")
    void sameFactTwoContracts() {
        Task07CourseCatalog catalog = new Task07CourseCatalog();
        Task07EnrollmentService service = new Task07EnrollmentService(catalog);
        service.addStudent(new Task00Student("S-1"));

        Optional<Task00Course> found = catalog.findCourse(Task10CourseCode.of("CS-101"));
        Task03EnrollmentRejectedException e = assertThrows(Task03EnrollmentRejectedException.class,
                () -> service.enroll("S-1", Task10CourseCode.of("CS-101")));

        assertAll(
                () -> assertTrue(found.isEmpty()),
                () -> assertEquals(Task03RejectionReason.COURSE_NOT_FOUND, e.reason()));
    }

    @Test
    @DisplayName("Контраст 2: тот же отказ значением — tryEnroll")
    void refusalAsValue() {
        Task07CourseCatalog catalog = new Task07CourseCatalog();
        catalog.add(course("CS-101", 1, 1));
        Task07EnrollmentService service = new Task07EnrollmentService(catalog);
        service.addStudent(new Task00Student("S-1"));

        Task07Result<Task00Course> result = service.tryEnroll("S-1", Task10CourseCode.of("CS-101"));

        assertAll(
                () -> assertFalse(result.isOk()),
                () -> assertEquals("CH-2004", result.errorCode()));
    }

    @Test
    @DisplayName("Граница ожидаемого результата защищена: только коды CH-2xxx")
    void resultAcceptsOnlyExpectedCodes() {
        assertAll(
                () -> assertEquals("CH-2004", Task07Result.fail("CH-2004").errorCode()),
                () -> assertThrows(Task02ValidationException.class, () -> Task07Result.fail("CH-4001")),
                () -> assertThrows(Task02ValidationException.class, () -> Task07Result.fail(null)));
    }

    @Test
    @DisplayName("calculatePrice: неверный аргумент — unchecked")
    void priceIsUnchecked() {
        Task07PriceCalculator calculator = new Task07PriceCalculator();

        assertAll(
                () -> assertThrows(Task02ValidationException.class, () -> calculator.calculatePrice(3, 0)),
                () -> assertThrows(Task02ValidationException.class, () -> calculator.calculatePrice(40, 101)),
                () -> assertEquals(3_600_000L, calculator.calculatePrice(40, 10)));
    }

    @Test
    @DisplayName("parseCourse: ошибка данных — checked")
    void parseIsChecked() {
        assertThrows(Task06CourseImportException.class, () -> new Task06CourseImporter().parseCourse("bad", 1));
    }

    @Test
    @DisplayName("Ошибка программиста не превращается в значение")
    void programmingErrorIsNotAValue() {
        Task07EnrollmentService service = new Task07EnrollmentService(new Task07CourseCatalog());

        assertThrows(Task02ValidationException.class, () -> service.tryEnroll("  ", Task10CourseCode.of("CS-101")));
    }
}
