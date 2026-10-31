package coursehub.practice8;

import java.util.List;
import java.util.Objects;

public record Course(
        String code,
        String title,
        int level,
        int duration,
        List<Module> modules) {

    public Course {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        if (duration < 0) {
            throw new IllegalArgumentException("duration must be >= 0");
        }
        modules = List.copyOf(Objects.requireNonNull(modules, "modules"));
        if (modules.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("modules must not contain null");
        }
    }

    public Course(String code, String title, int level, int duration) {
        this(code, title, level, duration, List.of());
    }

    public Course(String code, String title, int level, int duration, Module... modules) {
        this(code, title, level, duration, List.of(modules));
    }
}
