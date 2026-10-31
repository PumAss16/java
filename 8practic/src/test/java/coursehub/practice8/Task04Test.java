package coursehub.practice8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task04Test {

    @Test
    @DisplayName("уровни получают count и среднюю длительность за один проход")
    void groupsLevelsWithCountAndAverage() {
        List<Course> courses = List.of(
                new Course("C-1", "Alpha", 1, 60),
                new Course("C-2", "Beta", 1, 90),
                new Course("C-3", "Gamma", 2, 30));

        Map<Integer, Task4LevelStatistics.LevelSummary> result =
                Task4LevelStatistics.summarize(courses);

        assertEquals(List.of(1, 2), List.copyOf(result.keySet()));
        assertEquals(2, result.get(1).count());
        assertEquals(75.0, result.get(1).averageDuration(), 1e-9);
        assertEquals(1, result.get(2).count());
        assertEquals(30.0, result.get(2).averageDuration(), 1e-9);
    }

    @Test
    @DisplayName("пустой список даёт пустую статистику")
    void emptyListGivesEmptyMap() {
        assertTrue(Task4LevelStatistics.summarize(List.of()).isEmpty());
    }

    @Test
    @DisplayName("карта статистики неизменяемая")
    void summaryMapIsImmutable() {
        Map<Integer, Task4LevelStatistics.LevelSummary> result =
                Task4LevelStatistics.summarize(List.of(
                        new Course("C-1", "Alpha", 1, 60)));

        assertThrows(UnsupportedOperationException.class,
                () -> result.put(2, new Task4LevelStatistics.LevelSummary(0, 0)));
    }
}
