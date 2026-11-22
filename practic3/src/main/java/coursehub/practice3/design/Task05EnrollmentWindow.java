package coursehub.practice3.design;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class Task05EnrollmentWindow {

    private final Clock clock;

    public Task05EnrollmentWindow(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public boolean isOpen(Instant opensAt, Instant closesAt) {
        Objects.requireNonNull(opensAt, "opensAt");
        Objects.requireNonNull(closesAt, "closesAt");
        Instant now = clock.instant();
        return !now.isBefore(opensAt) && now.isBefore(closesAt);
    }
}
