package coursehub.practice3.rules;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.Objects;

public final class Task15AgeRule implements Task15EnrollmentRule {

    @Override
    public Task15RuleResult check(Task00EnrollmentRequest request) {
        Objects.requireNonNull(request, "request");
        if (request.student().age() < request.course().minAge()) {
            return Task15RuleResult.violated(
                    "age", "student age " + request.student().age()
                            + " is below " + request.course().minAge());
        }
        return Task15RuleResult.pass();
    }
}
