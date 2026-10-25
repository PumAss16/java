package coursehub.practice3.notify;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Task07LoggingSender implements Task02NotificationSender {

    private final Task02NotificationSender delegate;
    private final Consumer<String> logger;

    public Task07LoggingSender(Task02NotificationSender delegate, Consumer<String> logger) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    public String send(String message) {
        return logAndSend(message, () -> delegate.send(message));
    }

    @Override
    public String send(Object payload) {
        return logAndSend(payload, () -> delegate.send(payload));
    }

    private String logAndSend(Object value, Supplier<String> action) {
        logger.accept("before " + value);
        try {
            String result = action.get();
            logger.accept("after " + value + " -> " + result);
            return result;
        } catch (RuntimeException e) {
            logger.accept("error " + value + " -> " + e.getMessage());
            throw e;
        }
    }
}
