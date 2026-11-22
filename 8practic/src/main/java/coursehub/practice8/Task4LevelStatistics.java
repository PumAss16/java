package coursehub.practice8;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class Task4LevelStatistics {

    private Task4LevelStatistics() {
    }

    public record LevelSummary(long count, double averageDuration) {

        public LevelSummary {
            if (count < 0) {
                throw new IllegalArgumentException("count must be >= 0");
            }
            if (averageDuration < 0) {
                throw new IllegalArgumentException("averageDuration must be >= 0");
            }
        }
    }

    public static Map<Integer, LevelSummary> summarize(List<Course> courses) {
        Objects.requireNonNull(courses, "courses");
        Map<Integer, LevelSummary> result = courses.stream().collect(Collectors.groupingBy(
                Course::level,
                TreeMap::new,
                Collectors.teeing(
                        Collectors.counting(),
                        Collectors.averagingInt(Course::duration),
                        LevelSummary::new)));
        return Collections.unmodifiableMap(result);
    }
}
