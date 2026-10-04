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

/**
 * Задание 7. Выбор контракта для четырёх методов:
 * findCourse     -> Optional (отсутствие — нормальный результат поиска);
 * parseCourse    -> checked CourseImportException (данные извне, нужен номер записи и cause);
 * enroll         -> checked EnrollmentRejectedException (что показать пользователю, решает вызывающий);
 * calculatePrice -> unchecked ValidationException (неверный аргумент — ошибка программиста).
 * Контраст: tryEnroll отдаёт тот же отказ значением Result, но Result принимает
 * только ожидаемые коды — программные и инфраструктурные ошибки в него не попадают.
 */
@DisplayName("Задание 7. Значение или исключение")
class Task07Test {

    private Course course(String code, int capacity, int enrolled) {
        return new Course(CourseCode.of(code), "Course " + code, 40, capacity, enrolled, CourseStatus.ACTIVE, null);
    }

    @Test
    @DisplayName("findCourse: нет курса — Optional.empty, исключения нет")
    void findCourseReturnsOptional() {
        CourseCatalog catalog = new CourseCatalog();

        Optional<Course> found = catalog.findCourse(CourseCode.of("CS-101"));

        assertAll(
                () -> assertEquals(Optional.empty(), found),
                () -> assertNotNull(found));
    }

    @Test
    @DisplayName("Контраст 1: один факт — Optional в поиске и checked-отказ в enroll")
    void sameFactTwoContracts() {
        CourseCatalog catalog = new CourseCatalog();
        EnrollmentService service = new EnrollmentService(catalog);
        service.addStudent(new Student("S-1"));

        Optional<Course> found = catalog.findCourse(CourseCode.of("CS-101"));
        EnrollmentRejectedException e = assertThrows(EnrollmentRejectedException.class,
                () -> service.enroll("S-1", CourseCode.of("CS-101")));

        assertAll(
                () -> assertTrue(found.isEmpty()),
                () -> assertEquals(RejectionReason.COURSE_NOT_FOUND, e.reason()));
    }

    @Test
    @DisplayName("Контраст 2: тот же отказ значением — tryEnroll")
    void refusalAsValue() {
        CourseCatalog catalog = new CourseCatalog();
        catalog.add(course("CS-101", 1, 1));
        EnrollmentService service = new EnrollmentService(catalog);
        service.addStudent(new Student("S-1"));

        Result<Course> result = service.tryEnroll("S-1", CourseCode.of("CS-101"));

        assertAll(
                () -> assertFalse(result.isOk()),
                () -> assertEquals("CH-2004", result.errorCode()));
    }

    @Test
    @DisplayName("Граница ожидаемого результата защищена: только коды CH-2xxx")
    void resultAcceptsOnlyExpectedCodes() {
        assertAll(
                () -> assertEquals("CH-2004", Result.fail("CH-2004").errorCode()),
                () -> assertThrows(ValidationException.class, () -> Result.fail("CH-4001")),
                () -> assertThrows(ValidationException.class, () -> Result.fail(null)));
    }

    @Test
    @DisplayName("calculatePrice: неверный аргумент — unchecked")
    void priceIsUnchecked() {
        PriceCalculator calculator = new PriceCalculator();

        assertAll(
                () -> assertThrows(ValidationException.class, () -> calculator.calculatePrice(3, 0)),
                () -> assertThrows(ValidationException.class, () -> calculator.calculatePrice(40, 101)),
                () -> assertEquals(3_600_000L, calculator.calculatePrice(40, 10)));
    }

    @Test
    @DisplayName("parseCourse: ошибка данных — checked")
    void parseIsChecked() {
        assertThrows(CourseImportException.class, () -> new CourseImporter().parseCourse("bad", 1));
    }

    @Test
    @DisplayName("Ошибка программиста не превращается в значение")
    void programmingErrorIsNotAValue() {
        EnrollmentService service = new EnrollmentService(new CourseCatalog());

        assertThrows(ValidationException.class, () -> service.tryEnroll("  ", CourseCode.of("CS-101")));
    }
}
