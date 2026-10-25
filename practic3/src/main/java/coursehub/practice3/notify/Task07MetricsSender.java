package coursehub.practice3.notify;

import java.util.Objects;
import java.util.function.Supplier;

public final class Task07MetricsSender implements Task02NotificationSender {

    private final Task02NotificationSender delegate;
    private int attempts;
    private int successes;
    private int failures;

    public Task07MetricsSender(Task02NotificationSender delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public String send(String message) {
        return measure(() -> delegate.send(message));
    }

    @Override
    public String send(Object payload) {
        return measure(() -> delegate.send(payload));
    }

    public int attempts() {
        return attempts;
    }

    public int successes() {
        return successes;
    }

    public int failures() {
        return failures;
    }

    private String measure(Supplier<String> action) {
        attempts++;
        try {
            String result = action.get();
            successes++;
            return result;
        } catch (RuntimeException e) {
            failures++;
            throw e;
        }
    }
}
