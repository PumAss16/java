package coursehub.practice3.enrollment;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.Objects;

public final class Task09EnrollmentService {

    private final Task09EnrollmentPolicy policy;

    public Task09EnrollmentService(Task09EnrollmentPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    public Task12EnrollmentResult enroll(Task00EnrollmentRequest request) {
        return policy.evaluate(request);
    }
}
