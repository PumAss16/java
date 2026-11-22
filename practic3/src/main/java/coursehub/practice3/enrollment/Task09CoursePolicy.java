package coursehub.practice3.enrollment;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.Objects;

public abstract class Task09CoursePolicy implements Task09EnrollmentPolicy {

    @Override
    public final Task12EnrollmentResult evaluate(Task00EnrollmentRequest request) {
        Objects.requireNonNull(request, "request");
        return decide(request);
    }

    protected abstract Task12EnrollmentResult decide(Task00EnrollmentRequest request);
}
