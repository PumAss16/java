package coursehub.practice8;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task06Test {

    @Test
    @DisplayName("custom collector совпадает с summarizingInt")
    void customCollectorMatchesSummarizingInt() {
        List<Course> courses = List.of(
                new Course("C-1", "Alpha", 1, 10),
                new Course("C-2", "Beta", 1, 20),
                new Course("C-3", "Gamma", 2, 30));

        Task6CourseStats.Stats actual =
                courses.stream().collect(Task6CourseStats.collector());
        IntSummaryStatistics expected =
                courses.stream().collect(Collectors.summarizingInt(Course::duration));

        assertEquals(expected.getCount(), actual.count());
        assertEquals(expected.getSum(), actual.sum());
        assertEquals(expected.getMin(), actual.min());
        assertEquals(expected.getMax(), actual.max());
        assertEquals(expected.getAverage(), actual.average(), 1e-9);
    }

    @Test
    @DisplayName("combiner работает для parallel stream")
    void combinerWorksForParallelStream() {
        List<Course> courses = List.of(
                new Course("C-1", "Alpha", 1, 10),
                new Course("C-2", "Beta", 1, 20),
                new Course("C-3", "Gamma", 2, 30));

        Task6CourseStats.Stats sequential =
                courses.stream().collect(Task6CourseStats.collector());
        Task6CourseStats.Stats parallel =
                courses.parallelStream().collect(Task6CourseStats.collector());

        assertEquals(sequential, parallel);
    }

    @Test
    @DisplayName("пустой поток даёт нулевую статистику")
    void emptyStreamGivesZeroStats() {
        Task6CourseStats.Stats stats =
                List.<Course>of().stream().collect(Task6CourseStats.collector());

        assertEquals(new Task6CourseStats.Stats(0, 0, 0, 0), stats);
        assertEquals(0.0, stats.average(), 1e-9);
    }
}
