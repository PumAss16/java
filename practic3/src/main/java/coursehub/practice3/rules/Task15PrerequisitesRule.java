package coursehub.practice3.rules;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Task15PrerequisitesRule implements Task15EnrollmentRule {

    @Override
    public Task15RuleResult check(Task00EnrollmentRequest request) {
        Objects.requireNonNull(request, "request");
        Set<String> missing = new LinkedHashSet<>(request.course().prerequisites());
        missing.removeAll(request.student().completedCourses());
        if (!missing.isEmpty()) {
            return Task15RuleResult.violated(
                    "prerequisites", "missing: " + String.join(", ", missing));
        }
        return Task15RuleResult.pass();
    }
}
