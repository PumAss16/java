package coursehub.practice8;

import java.util.Objects;

public record EnrollmentEvent(String studentId, String courseCode, EventType type) {

    public EnrollmentEvent {
        Objects.requireNonNull(type, "type");
    }
}
