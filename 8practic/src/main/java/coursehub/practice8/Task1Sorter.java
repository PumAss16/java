package coursehub.practice8;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class Task1Sorter {

    public static final Comparator<Course> ORDER = Comparator
            .comparingInt(Course::level)
            .thenComparingInt(Course::duration)
            .thenComparing(Course::code)
            .thenComparing(Course::title);

    private Task1Sorter() {
    }

    public static List<Course> sort(List<Course> courses) {
        Objects.requireNonNull(courses, "courses");
        return courses.stream().sorted(ORDER).toList();
    }
}
