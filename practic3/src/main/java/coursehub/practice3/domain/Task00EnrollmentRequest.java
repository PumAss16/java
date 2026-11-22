package coursehub.practice3.domain;

import java.util.Objects;

public record Task00EnrollmentRequest(Task00Student student, Task00Course course, int enrolledCount) {

    public Task00EnrollmentRequest {
        Objects.requireNonNull(student, "student");
        Objects.requireNonNull(course, "course");
        if (enrolledCount < 0) {
            throw new IllegalArgumentException("enrolledCount must be >= 0");
        }
    }
}
