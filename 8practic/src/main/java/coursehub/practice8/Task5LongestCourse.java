package coursehub.practice8;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Task5LongestCourse {

    private static final Comparator<Course> BY_DURATION_THEN_CODE = Comparator
            .comparingInt(Course::duration)
            .thenComparing(Course::code);

    private Task5LongestCourse() {
    }

    public static Optional<Course> longest(List<Course> courses) {
        Objects.requireNonNull(courses, "courses");
        return courses.stream().max(BY_DURATION_THEN_CODE);
    }

    public static String titleOrFallback(List<Course> courses, String fallback) {
        if (fallback == null || fallback.isBlank()) {
            throw new IllegalArgumentException("fallback must not be blank");
        }
        return longest(courses).map(Course::title).orElse(fallback);
    }
}
