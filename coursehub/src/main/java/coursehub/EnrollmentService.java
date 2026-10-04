package coursehub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Зачисление (задания 7, 13, 15).
 * Ожидаемый отказ — checked EnrollmentRejectedException,
 * ошибка программиста — unchecked ValidationException.
 */
public class EnrollmentService {

    private final CourseCatalog catalog;
    private final EnrollmentPolicy policy = new EnrollmentPolicy();
    private final Map<String, Student> students = new HashMap<>();
    private final Set<String> enrollments = new HashSet<>();

    public EnrollmentService(CourseCatalog catalog) {
        this.catalog = catalog;
    }

    public void addStudent(Student student) {
        students.put(student.id(), student);
    }

    public Course enroll(String studentId, CourseCode code) throws EnrollmentRejectedException {
        if (studentId == null || studentId.isBlank() || code == null) {
            throw new ValidationException("studentId and course code are required");
        }
        Course course = catalog.findCourse(code)
                .orElseThrow(() -> new EnrollmentRejectedException(studentId, code.value(), RejectionReason.COURSE_NOT_FOUND));
        Student student = Optional.ofNullable(students.get(studentId))
                .orElseThrow(() -> new EnrollmentRejectedException(studentId, code.value(), RejectionReason.STUDENT_NOT_FOUND));

        boolean again = enrollments.contains(studentId + code.value());
        Optional<RejectionReason> refusal = policy.refusal(student, course, again);
        if (refusal.isPresent()) {
            throw new EnrollmentRejectedException(studentId, code.value(), refusal.get());
        }

        course.reserveSeat();
        enrollments.add(studentId + code.value());
        return course;
    }

    /** Контрастный контракт: тот же отказ, но значением (задание 7). */
    public Result<Course> tryEnroll(String studentId, CourseCode code) {
        try {
            return Result.ok(enroll(studentId, code));
        } catch (EnrollmentRejectedException e) {
            return Result.fail(e.code());
        }
    }
}
