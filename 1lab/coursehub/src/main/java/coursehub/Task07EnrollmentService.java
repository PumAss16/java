package coursehub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Task07EnrollmentService {

    private final Task07CourseCatalog catalog;
    private final Task13EnrollmentPolicy policy = new Task13EnrollmentPolicy();
    private final Map<String, Task00Student> students = new HashMap<>();
    private final Set<String> enrollments = new HashSet<>();

    public Task07EnrollmentService(Task07CourseCatalog catalog) {
        this.catalog = catalog;
    }

    public void addStudent(Task00Student student) {
        students.put(student.id(), student);
    }

    public Task00Course enroll(String studentId, Task10CourseCode code) throws Task03EnrollmentRejectedException {
        if (studentId == null || studentId.isBlank() || code == null) {
            throw new Task02ValidationException("studentId and course code are required");
        }
        Task00Course course = catalog.findCourse(code)
                .orElseThrow(() -> new Task03EnrollmentRejectedException(studentId, code.value(), Task03RejectionReason.COURSE_NOT_FOUND));
        Task00Student student = Optional.ofNullable(students.get(studentId))
                .orElseThrow(() -> new Task03EnrollmentRejectedException(studentId, code.value(), Task03RejectionReason.STUDENT_NOT_FOUND));

        boolean again = enrollments.contains(studentId + code.value());
        Optional<Task03RejectionReason> refusal = policy.refusal(student, course, again);
        if (refusal.isPresent()) {
            throw new Task03EnrollmentRejectedException(studentId, code.value(), refusal.get());
        }

        course.reserveSeat();
        enrollments.add(studentId + code.value());
        return course;
    }

    public Task07Result<Task00Course> tryEnroll(String studentId, Task10CourseCode code) {
        try {
            return Task07Result.ok(enroll(studentId, code));
        } catch (Task03EnrollmentRejectedException e) {
            return Task07Result.fail(e.code());
        }
    }
}
