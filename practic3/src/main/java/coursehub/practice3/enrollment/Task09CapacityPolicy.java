package coursehub.practice3.enrollment;

import coursehub.practice3.domain.Task00EnrollmentRequest;

public final class Task09CapacityPolicy extends Task09CoursePolicy {

    @Override
    protected Task12EnrollmentResult decide(Task00EnrollmentRequest request) {
        if (request.enrolledCount() >= request.course().capacity()) {
            int position = request.enrolledCount() - request.course().capacity() + 1;
            return new Task12EnrollmentResult.WaitListed(request.course().code(), Math.max(1, position));
        }
        return new Task12EnrollmentResult.Accepted(request.course().code());
    }
}
