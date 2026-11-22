package coursehub.practice8;

import java.util.IntSummaryStatistics;
import java.util.stream.Collector;

public final class Task6CourseStats {

    private Task6CourseStats() {
    }

    public record Stats(long count, long sum, int min, int max) {

        public Stats {
            if (count < 0) {
                throw new IllegalArgumentException("count must be >= 0");
            }
            if (sum < 0) {
                throw new IllegalArgumentException("sum must be >= 0");
            }
            if (count == 0 && (min != 0 || max != 0)) {
                throw new IllegalArgumentException("empty stats must have zero min and max");
            }
            if (count > 0 && min > max) {
                throw new IllegalArgumentException("min must be <= max");
            }
        }

        public double average() {
            return count == 0 ? 0.0 : (double) sum / count;
        }
    }

    public static Collector<Course, ?, Stats> collector() {
        return Collector.of(
                IntSummaryStatistics::new,
                (stats, course) -> stats.accept(course.duration()),
                Task6CourseStats::combine,
                Task6CourseStats::finish);
    }

    private static IntSummaryStatistics combine(IntSummaryStatistics left,
                                                IntSummaryStatistics right) {
        left.combine(right);
        return left;
    }

    private static Stats finish(IntSummaryStatistics stats) {
        if (stats.getCount() == 0) {
            return new Stats(0, 0, 0, 0);
        }
        return new Stats(stats.getCount(), stats.getSum(), stats.getMin(), stats.getMax());
    }
}
