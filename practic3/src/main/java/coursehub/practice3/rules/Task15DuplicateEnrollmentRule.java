package coursehub.practice3.rules;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.Objects;

public final class Task15DuplicateEnrollmentRule implements Task15EnrollmentRule {

    @Override
    public Task15RuleResult check(Task00EnrollmentRequest request) {
        Objects.requireNonNull(request, "request");
        if (request.student().enrolledCourses().contains(request.course().code())) {
            return Task15RuleResult.violated(
                    "duplicate", "student is already enrolled in " + request.course().code());
        }
        return Task15RuleResult.pass();
    }
}
