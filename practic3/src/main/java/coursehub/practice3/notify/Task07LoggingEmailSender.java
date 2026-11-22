package coursehub.practice3.notify;

import java.util.Objects;
import java.util.function.Consumer;

public final class Task07LoggingEmailSender extends Task02EmailSender {

    private final Consumer<String> logger;

    public Task07LoggingEmailSender(Consumer<String> logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    public String send(String message) {
        logger.accept("LOG email: " + message);
        return super.send(message);
    }
}
