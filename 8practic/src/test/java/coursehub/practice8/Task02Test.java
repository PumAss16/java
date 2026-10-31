package coursehub.practice8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task02Test {

    @Test
    @DisplayName("flatMap нормализует темы и строит обратный индекс")
    void flatMapBuildsTopicsAndIndex() {
        Course java = new Course("C-2", "Java", 1, 120,
                new Module("Basics", List.of(" Java ", "Collections")),
                new Module("Advanced", List.of("java", "Streams")));
        Course sql = new Course("C-1", "SQL", 1, 90,
                new Module("Queries", List.of("  SQL  ", "java")));

        Task2TopicIndex.Result result = Task2TopicIndex.build(List.of(java, sql));

        assertEquals(List.of("collections", "java", "sql", "streams"), result.topics());
        assertEquals(List.of(sql, java), result.byTopic().get("java"));
        assertEquals(List.of(java), result.byTopic().get("collections"));
        assertEquals("java streams", Task2TopicIndex.normalize("  Java   Streams  "));
    }

    @Test
    @DisplayName("индекс не отдаёт изменяемую карту")
    void indexIsImmutable() {
        Task2TopicIndex.Result result = Task2TopicIndex.build(List.of());

        assertThrows(UnsupportedOperationException.class,
                () -> result.byTopic().put("java", List.of()));
        assertThrows(UnsupportedOperationException.class,
                () -> result.topics().add("java"));
    }
}
