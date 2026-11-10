package coursehub;

import java.util.Optional;

public class Task13EnrollmentPolicy {

    public Optional<Task03RejectionReason> refusal(Task00Student student, Task00Course course, boolean alreadyEnrolled) {
        if (alreadyEnrolled) {
            return Optional.of(Task03RejectionReason.ALREADY_ENROLLED);
        }
        if (course.status() != Task13CourseStatus.ACTIVE) {
            return Optional.of(Task03RejectionReason.COURSE_NOT_ACTIVE);
        }
        if (course.prerequisite() != null && !student.hasCompleted(course.prerequisite())) {
            return Optional.of(Task03RejectionReason.PREREQUISITE_NOT_MET);
        }
        if (!course.hasFreeSeat()) {
            return Optional.of(Task03RejectionReason.COURSE_FULL);
        }
        return Optional.empty();
    }
}
