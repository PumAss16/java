package coursehub.practice3.notify;

import java.util.Objects;

public final class Task07FlakySender implements Task02NotificationSender {

    private final Task02NotificationSender delegate;
    private int failuresLeft;

    public Task07FlakySender(Task02NotificationSender delegate, int failures) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        if (failures < 0) {
            throw new IllegalArgumentException("failures must be >= 0");
        }
        this.failuresLeft = failures;
    }

    @Override
    public String send(String message) {
        failIfNeeded();
        return delegate.send(message);
    }

    @Override
    public String send(Object payload) {
        failIfNeeded();
        return delegate.send(payload);
    }

    private void failIfNeeded() {
        if (failuresLeft > 0) {
            failuresLeft--;
            throw new IllegalStateException("temporary delivery failure");
        }
    }
}
