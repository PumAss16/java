package coursehub;

public class Task03EnrollmentRejectedException extends Exception {

    private final String studentId;
    private final String courseId;
    private final Task03RejectionReason reason;

    public Task03EnrollmentRejectedException(String studentId, String courseId, Task03RejectionReason reason) {
        this(studentId, courseId, reason, null);
    }

    public Task03EnrollmentRejectedException(String studentId, String courseId, Task03RejectionReason reason, Throwable cause) {
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

    public Task03RejectionReason reason() {
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
