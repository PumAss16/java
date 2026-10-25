package coursehub.practice3.rules;

import coursehub.practice3.domain.Task00EnrollmentRequest;
import java.util.Objects;

public final class Task15ScheduleConflictRule implements Task15EnrollmentRule {

    @Override
    public Task15RuleResult check(Task00EnrollmentRequest request) {
        Objects.requireNonNull(request, "request");
        if (request.student().busySlots().contains(request.course().scheduleSlot())) {
            return Task15RuleResult.violated(
                    "schedule", "conflict at " + request.course().scheduleSlot());
        }
        return Task15RuleResult.pass();
    }
}
