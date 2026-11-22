package coursehub.practice3.enrollment;

import coursehub.practice3.domain.Task00EnrollmentRequest;

public final class Task09AgePolicy extends Task09CoursePolicy {

    @Override
    protected Task12EnrollmentResult decide(Task00EnrollmentRequest request) {
        if (request.student().age() < request.course().minAge()) {
            return new Task12EnrollmentResult.Rejected(
                    "age " + request.student().age() + " is below " + request.course().minAge());
        }
        return new Task12EnrollmentResult.Accepted(request.course().code());
    }
}
