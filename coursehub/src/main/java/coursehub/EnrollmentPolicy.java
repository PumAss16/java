package coursehub;

import java.util.Optional;

/**
 * Таблица решений зачисления (задание 13).
 * Правила применяются по старшинству, остальные условия становятся don't care:
 * R1 повторная заявка, R2 статус курса, R3 prerequisite, R4 наличие мест.
 */
public class EnrollmentPolicy {

    /** @return причина отказа или Optional.empty(), если можно зачислить */
    public Optional<RejectionReason> refusal(Student student, Course course, boolean alreadyEnrolled) {
        if (alreadyEnrolled) {
            return Optional.of(RejectionReason.ALREADY_ENROLLED);
        }
        if (course.status() != CourseStatus.ACTIVE) {
            return Optional.of(RejectionReason.COURSE_NOT_ACTIVE);
        }
        if (course.prerequisite() != null && !student.hasCompleted(course.prerequisite())) {
            return Optional.of(RejectionReason.PREREQUISITE_NOT_MET);
        }
        if (!course.hasFreeSeat()) {
            return Optional.of(RejectionReason.COURSE_FULL);
        }
        return Optional.empty();
    }
}
