package coursehub;

/**
 * Ожидаемый отказ в зачислении (задание 3): studentId, courseId и reason code.
 * studentId — персональные данные, поэтому в message и toString его нет,
 * но программный доступ через {@link #studentId()} сохранён.
 */
public class EnrollmentRejectedException extends Exception {

    private final String studentId;
    private final String courseId;
    private final RejectionReason reason;

    public EnrollmentRejectedException(String studentId, String courseId, RejectionReason reason) {
        this(studentId, courseId, reason, null);
    }

    /** Исходная причина при оборачивании сохраняется в cause. */
    public EnrollmentRejectedException(String studentId, String courseId, RejectionReason reason, Throwable cause) {
        super("enrollment rejected: course=" + courseId + ", reason=" + reason, cause);
        this.studentId = studentId;
        this.courseId = courseId;
        this.reason = reason;
    }

    public String studentId() {
        return studentId;
    }

    public String courseId() {
        return courseId;
    }

    public RejectionReason reason() {
        return reason;
    }

    public String code() {
        return reason.code();
    }

    @Override
    public String toString() {
        return "EnrollmentRejectedException[course=" + courseId + ", reason=" + reason + "]";
    }
}
