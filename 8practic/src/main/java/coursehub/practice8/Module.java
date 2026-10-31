package coursehub.practice8;

import java.util.List;
import java.util.Objects;

public record Module(String title, List<String> topics) {

    public Module {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        topics = List.copyOf(Objects.requireNonNull(topics, "topics"));
        if (topics.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("topics must not contain null");
        }
    }
}
