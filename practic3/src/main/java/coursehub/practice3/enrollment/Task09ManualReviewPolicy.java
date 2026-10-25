package coursehub.practice3.enrollment;

import coursehub.practice3.domain.Task00EnrollmentRequest;

public final class Task09ManualReviewPolicy extends Task09CoursePolicy {

    @Override
    protected Task12EnrollmentResult decide(Task00EnrollmentRequest request) {
        if ("TBD".equalsIgnoreCase(request.course().scheduleSlot())) {
            return new Task12EnrollmentResult.UnderReview(request.course().code());
        }
        return new Task12EnrollmentResult.Accepted(request.course().code());
    }
}
