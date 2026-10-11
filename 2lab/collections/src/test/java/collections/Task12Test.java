package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task12Test {

    private static Course course(String id, String title) {
        return new Course(CourseId.of(id), CourseCode.of(id), title);
    }

    @Test
    @DisplayName("сохранение новой сущности даёт пустой Optional")
    void saveNewEntityReturnsEmpty() {
        Repository<CourseId, Course> repo = new InMemoryRepository<>();
        Course java = course("C-1", "Java");
        assertEquals(Optional.empty(), repo.save(java.id(), java));
        assertEquals(Optional.of(java), repo.findById(java.id()));
        assertEquals(1, repo.size());
    }

    @Test
    @DisplayName("повторный save заменяет значение и отдаёт прежнее")
    void repeatedSaveReplacesValue() {
        Repository<CourseId, Course> repo = new InMemoryRepository<>();
        Course first = course("C-1", "Java");
        Course second = course("C-1", "Java advanced");
        repo.save(first.id(), first);
        Optional<Course> previous = repo.save(second.id(), second);
        assertEquals(Optional.of(first), previous);
        assertEquals("Java advanced", repo.findById(first.id()).orElseThrow().title());
        assertEquals(1, repo.size());
    }

    @Test
    @DisplayName("findAll возвращает неизменяемый снимок")
    void findAllReturnsImmutableSnapshot() {
        Repository<CourseId, Course> repo = new InMemoryRepository<>();
        Course java = course("C-1", "Java");
        repo.save(java.id(), java);
        List<Course> snapshot = repo.findAll();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(course("C-2", "SQL")));
        repo.save(CourseId.of("C-2"), course("C-2", "SQL"));
        assertEquals(List.of(java), snapshot);
        assertEquals(2, repo.findAll().size());
    }

    @Test
    @DisplayName("null-аргументы отклоняются с NullPointerException")
    void nullArgumentsAreRejected() {
        Repository<CourseId, Course> repo = new InMemoryRepository<>();
        Course java = course("C-1", "Java");
        CourseId id = java.id();
        assertThrows(NullPointerException.class, () -> repo.save(null, java));
        assertThrows(NullPointerException.class, () -> repo.save(id, null));
        assertThrows(NullPointerException.class, () -> repo.findById(null));
        assertThrows(NullPointerException.class, () -> repo.deleteById(null));
    }

    @Test
    @DisplayName("deleteById удаляет запись и сообщает результат")
    void deleteByIdReportsPresence() {
        Repository<CourseId, Course> repo = new InMemoryRepository<>();
        Course java = course("C-1", "Java");
        repo.save(java.id(), java);
        assertTrue(repo.deleteById(java.id()));
        assertFalse(repo.deleteById(java.id()));
        assertEquals(0, repo.size());
        assertEquals(Optional.empty(), repo.findById(java.id()));
    }

    @Test
    @DisplayName("репозиторий работает с другой парой типов")
    void repositoryWorksWithOtherTypes() {
        Repository<String, Integer> repo = new InMemoryRepository<>();
        assertEquals(Optional.empty(), repo.save("one", 1));
        repo.save("two", 2);
        assertEquals(Optional.of(1), repo.save("one", 11));
        assertEquals(Optional.of(11), repo.findById("one"));
        assertEquals(List.of(11, 2), repo.findAll());
        assertEquals(2, repo.size());
    }
}
