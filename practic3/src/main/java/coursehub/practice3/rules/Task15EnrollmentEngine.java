package coursehub.practice3.rules;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import coursehub.practice3.enrollment.Task12EnrollmentResult;
import java.util.Objects;

public final class Task15EnrollmentEngine {

    private final Task15EnrollmentRule rule;

    public Task15EnrollmentEngine(Task15EnrollmentRule rule) {
        this.rule = Objects.requireNonNull(rule, "rule");
    }

    public Task12EnrollmentResult enroll(Task00EnrollmentRequest request) {
        Objects.requireNonNull(request, "request");
        Task15RuleResult result = rule.check(request);
        return switch (result) {
            case Task15RuleResult.Passed passed ->
                    new Task12EnrollmentResult.Accepted(request.course().code());
            case Task15RuleResult.Violated violation ->
                    new Task12EnrollmentResult.Rejected(violation.code() + ": " + violation.message());
        };
    }
}
