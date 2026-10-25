package coursehub.practice3.enrollment;

import coursehub.practice3.domain.Task00EnrollmentRequest;

@FunctionalInterface
public interface Task09EnrollmentPolicy {

    Task12EnrollmentResult evaluate(Task00EnrollmentRequest request);
}
