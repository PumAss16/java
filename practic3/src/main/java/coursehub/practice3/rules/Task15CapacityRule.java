package coursehub.practice3.rules;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.Objects;

public final class Task15CapacityRule implements Task15EnrollmentRule {

    @Override
    public Task15RuleResult check(Task00EnrollmentRequest request) {
        Objects.requireNonNull(request, "request");
        if (request.enrolledCount() >= request.course().capacity()) {
            return Task15RuleResult.violated(
                    "capacity", "no seats left for " + request.course().code());
        }
        return Task15RuleResult.pass();
    }
}
