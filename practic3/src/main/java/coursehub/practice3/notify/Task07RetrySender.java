package coursehub.practice3.notify;

import java.util.Objects;
import java.util.function.Supplier;

public final class Task07RetrySender implements Task02NotificationSender {

    private final Task02NotificationSender delegate;
    private final int maxAttempts;

    public Task07RetrySender(Task02NotificationSender delegate, int maxAttempts) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        this.maxAttempts = maxAttempts;
    }

    @Override
    public String send(String message) {
        return execute(() -> delegate.send(message));
    }

    @Override
    public String send(Object payload) {
        return execute(() -> delegate.send(payload));
    }

    private String execute(Supplier<String> action) {
        RuntimeException last = new IllegalStateException("no attempts");
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return action.get();
            } catch (RuntimeException e) {
                last = e;
            }
        }
        throw last;
    }
}
