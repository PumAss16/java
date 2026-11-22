package coursehub.practice3.domain;

import coursehub.practice3.design.Task05Identifiable;
import java.util.Objects;
import java.util.Set;

public record Task00Course(
        String code,
        String title,
        int minAge,
        int capacity,
        Set<String> prerequisites,
        String scheduleSlot) implements Task05Identifiable {

    public Task00Course {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (minAge < 0) {
            throw new IllegalArgumentException("minAge must be >= 0");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must be >= 0");
        }
        prerequisites = Set.copyOf(Objects.requireNonNull(prerequisites, "prerequisites"));
        if (scheduleSlot == null || scheduleSlot.isBlank()) {
            throw new IllegalArgumentException("scheduleSlot must not be blank");
        }
    }

    public Task00Course(String code, String title) {
        this(code, title, 0, 100, Set.of(), "none");
    }

    @Override
    public String id() {
        return code;
    }
}
