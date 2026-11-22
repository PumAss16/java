package coursehub.practice3.rules;

import coursehub.practice3.domain.Task00EnrollmentRequest;

@FunctionalInterface
public interface Task15EnrollmentRule {

    Task15RuleResult check(Task00EnrollmentRequest request);
}
