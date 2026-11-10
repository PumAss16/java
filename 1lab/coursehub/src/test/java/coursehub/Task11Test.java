package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Задание 11. Состояние после отказа")
class Task11Test {

    @Test
    @DisplayName("Курс полон: код CH-2004, поля исключения и неизменное состояние")
    void failedEnrollmentChangesNothing() {
        Task00Course course = new Task00Course(Task10CourseCode.of("CS-101"), "Java", 40, 1, 1, Task13CourseStatus.ACTIVE, null);
        Task07CourseCatalog catalog = new Task07CourseCatalog();
        catalog.add(course);
        Task07EnrollmentService service = new Task07EnrollmentService(catalog);
        service.addStudent(new Task00Student("STU-42"));

        Task03EnrollmentRejectedException e = assertThrows(Task03EnrollmentRejectedException.class,
                () -> service.enroll("STU-42", course.code()));

        assertAll(
                () -> assertEquals(Task03RejectionReason.COURSE_FULL, e.reason()),
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
        Task00Course course = new Task00Course(Task10CourseCode.of("CS-101"), "Java", 40, 30, 0, Task13CourseStatus.ACTIVE, null);

        Task02ValidationException e = assertThrows(Task02ValidationException.class, () -> course.completeHours(41));

        assertAll(
                () -> assertEquals(Task02ValidationException.class, e.getClass()),
                () -> assertTrue(e.getMessage().contains("41")),
                () -> assertEquals(0, course.completed(), "прогресс не изменился"),
                () -> assertEquals(40, course.completeHours(40), "курс остался рабочим"));
    }

    @Test
    @DisplayName("Отказ не портит сервис: следующий студент зачисляется")
    void rejectionDoesNotPoisonTheService() throws Task03EnrollmentRejectedException {
        Task10CourseCode math = Task10CourseCode.of("MATH-101");
        Task00Course course = new Task00Course(Task10CourseCode.of("CS-202"), "Algorithms", 40, 30, 0, Task13CourseStatus.ACTIVE, math);
        Task07CourseCatalog catalog = new Task07CourseCatalog();
        catalog.add(course);
        Task07EnrollmentService service = new Task07EnrollmentService(catalog);
        service.addStudent(new Task00Student("S-1"));
        service.addStudent(new Task00Student("S-2", math));

        Task03EnrollmentRejectedException e = assertThrows(Task03EnrollmentRejectedException.class,
                () -> service.enroll("S-1", course.code()));
        Task00Course enrolled = service.enroll("S-2", course.code());

        assertAll(
                () -> assertEquals(Task03RejectionReason.PREREQUISITE_NOT_MET, e.reason()),
                () -> assertEquals(1, course.enrolled()),
                () -> assertEquals("CS-202", enrolled.code().value()));
    }
}
