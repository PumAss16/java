package coursehub.practice3.rules;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.List;
import java.util.Objects;

public final class Task15AllOfRule implements Task15EnrollmentRule {

    private final List<Task15EnrollmentRule> rules;

    public Task15AllOfRule(Task15EnrollmentRule... rules) {
        this(List.of(rules));
    }

    public Task15AllOfRule(List<Task15EnrollmentRule> rules) {
        Objects.requireNonNull(rules, "rules");
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("rules must not be empty");
        }
        this.rules = List.copyOf(rules);
    }

    @Override
    public Task15RuleResult check(Task00EnrollmentRequest request) {
        Objects.requireNonNull(request, "request");
        for (Task15EnrollmentRule rule : rules) {
            Task15RuleResult result = rule.check(request);
            if (result instanceof Task15RuleResult.Violated) {
                return result;
            }
        }
        return Task15RuleResult.pass();
    }
}
