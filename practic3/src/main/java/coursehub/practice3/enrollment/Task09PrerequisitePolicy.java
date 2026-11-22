package coursehub.practice3.enrollment;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Task09PrerequisitePolicy extends Task09CoursePolicy {

    @Override
    protected Task12EnrollmentResult decide(Task00EnrollmentRequest request) {
        Set<String> missing = new LinkedHashSet<>(request.course().prerequisites());
        missing.removeAll(request.student().completedCourses());
        if (!missing.isEmpty()) {
            return new Task12EnrollmentResult.Rejected(
                    "missing prerequisites: " + String.join(", ", missing));
        }
        return new Task12EnrollmentResult.Accepted(request.course().code());
    }
}
