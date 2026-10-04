package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Задание 11. assertThrows: проверяем тип, код и поля исключения, а также то,
 * что объект не изменился после неуспешной команды.
 * В lambda — ровно одно потенциально падающее действие, иначе непонятно,
 * что именно бросило исключение.
 */
@DisplayName("Задание 11. Состояние после отказа")
class Task11Test {

    @Test
    @DisplayName("Курс полон: код CH-2004, поля исключения и неизменное состояние")
    void failedEnrollmentChangesNothing() {
        Course course = new Course(CourseCode.of("CS-101"), "Java", 40, 1, 1, CourseStatus.ACTIVE, null);
        CourseCatalog catalog = new CourseCatalog();
        catalog.add(course);
        EnrollmentService service = new EnrollmentService(catalog);
        service.addStudent(new Student("STU-42"));

        EnrollmentRejectedException e = assertThrows(EnrollmentRejectedException.class,
                () -> service.enroll("STU-42", course.code()));

        assertAll(
                () -> assertEquals(RejectionReason.COURSE_FULL, e.reason()),
                () -> assertEquals("CH-2004", e.code()),
                () -> assertEquals("STU-42", e.studentId()),
                () -> assertEquals("CS-101", e.courseId()),
                () -> assertFalse(e.getMessage().contains("STU-42")),
                () -> assertEquals(1, course.enrolled(), "место не должно быть занято"),
                () -> assertEquals("CH-2004", service.tryEnroll("STU-42", course.code()).errorCode()));
    }

    @Test
    @DisplayName("completeHours сверх программы: точный тип и неизменный прогресс")
    void failedCompleteHoursKeepsProgress() {
        Course course = new Course(CourseCode.of("CS-101"), "Java", 40, 30, 0, CourseStatus.ACTIVE, null);

        ValidationException e = assertThrows(ValidationException.class, () -> course.completeHours(41));

        assertAll(
                () -> assertEquals(ValidationException.class, e.getClass()),
                () -> assertTrue(e.getMessage().contains("41")),
                () -> assertEquals(0, course.completed(), "прогресс не изменился"),
                () -> assertEquals(40, course.completeHours(40), "курс остался рабочим"));
    }

    @Test
    @DisplayName("Отказ не портит сервис: следующий студент зачисляется")
    void rejectionDoesNotPoisonTheService() throws EnrollmentRejectedException {
        CourseCode math = CourseCode.of("MATH-101");
        Course course = new Course(CourseCode.of("CS-202"), "Algorithms", 40, 30, 0, CourseStatus.ACTIVE, math);
        CourseCatalog catalog = new CourseCatalog();
        catalog.add(course);
        EnrollmentService service = new EnrollmentService(catalog);
        service.addStudent(new Student("S-1"));
        service.addStudent(new Student("S-2", math));

        EnrollmentRejectedException e = assertThrows(EnrollmentRejectedException.class,
                () -> service.enroll("S-1", course.code()));
        Course enrolled = service.enroll("S-2", course.code());

        assertAll(
                () -> assertEquals(RejectionReason.PREREQUISITE_NOT_MET, e.reason()),
                () -> assertEquals(1, course.enrolled()),
                () -> assertEquals("CS-202", enrolled.code().value()));
    }
}
