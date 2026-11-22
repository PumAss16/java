package coursehub.practice8;

import java.util.List;
import java.util.Objects;

public final class Task3Totals {

    private Task3Totals() {
    }

    public record Totals(long minutes, int courses) {

        public Totals {
            if (minutes < 0) {
                throw new IllegalArgumentException("minutes must be >= 0");
            }
            if (courses < 0) {
                throw new IllegalArgumentException("courses must be >= 0");
            }
        }

        public Totals plus(Totals other) {
            Objects.requireNonNull(other, "other");
            return new Totals(minutes + other.minutes, courses + other.courses);
        }
    }

    public static Totals identity() {
        return new Totals(0, 0);
    }

    public static Totals wrongIdentity() {
        return new Totals(10, 1);
    }

    public static Totals add(Totals totals, Course course) {
        Objects.requireNonNull(totals, "totals");
        Objects.requireNonNull(course, "course");
        return totals.plus(new Totals(course.duration(), 1));
    }

    public static Totals sum(List<Course> courses) {
        return sum(courses, identity());
    }

    public static Totals sum(List<Course> courses, Totals identity) {
        Objects.requireNonNull(courses, "courses");
        Objects.requireNonNull(identity, "identity");
        return courses.stream().reduce(identity, Task3Totals::add, Totals::plus);
    }

    public static Totals sumParallel(List<Course> courses, Totals identity) {
        Objects.requireNonNull(courses, "courses");
        Objects.requireNonNull(identity, "identity");
        return courses.parallelStream().reduce(identity, Task3Totals::add, Totals::plus);
    }
}
