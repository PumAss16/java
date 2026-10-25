package coursehub.practice3.domain;

import coursehub.practice3.design.Task05Identifiable;
import java.util.Objects;
import java.util.Set;

public record Task00Student(
        String id,
        int age,
        Set<String> completedCourses,
        Set<String> enrolledCourses,
        Set<String> busySlots) implements Task05Identifiable {

    public Task00Student {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (age < 0) {
            throw new IllegalArgumentException("age must be >= 0");
        }
        completedCourses = Set.copyOf(
                Objects.requireNonNull(completedCourses, "completedCourses"));
        enrolledCourses = Set.copyOf(
                Objects.requireNonNull(enrolledCourses, "enrolledCourses"));
        busySlots = Set.copyOf(Objects.requireNonNull(busySlots, "busySlots"));
    }

    public Task00Student(String id, int age) {
        this(id, age, Set.of(), Set.of(), Set.of());
    }
}
