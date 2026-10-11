package collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task13Test {

    private static Course course(String id, String title, String... tags) {
        Tag[] parsed = new Tag[tags.length];
        for (int i = 0; i < tags.length; i++) {
            parsed[i] = Tag.of(tags[i]);
        }
        return new Course(CourseId.of(id), CourseCode.of("C-" + id), title, parsed);
    }

    private static TagIndex indexWithThreeCourses() {
        TagIndex index = new TagIndex();
        index.add(course("1", "Java", "java", "oop"));
        index.add(course("2", "Algorithms", "java"));
        index.add(course("3", "Web", "java", "oop", "web"));
        return index;
    }

    @Test
    @DisplayName("byTag возвращает id курсов в порядке добавления")
    void byTagReturnsIdsInInsertionOrder() {
        TagIndex index = indexWithThreeCourses();

        assertAll(
                () -> assertEquals(List.of(CourseId.of("1"), CourseId.of("2"), CourseId.of("3")),
                        new ArrayList<>(index.byTag(Tag.of("java")))),
                () -> assertEquals(List.of(CourseId.of("1"), CourseId.of("3")),
                        new ArrayList<>(index.byTag(Tag.of("oop")))),
                () -> assertEquals(List.of(CourseId.of("3")),
                        new ArrayList<>(index.byTag(Tag.of("web"))))
        );
    }

    @Test
    @DisplayName("byAllTags возвращает пересечение тегов")
    void byAllTagsReturnsIntersection() {
        TagIndex index = indexWithThreeCourses();

        assertAll(
                () -> assertEquals(Set.of(CourseId.of("1"), CourseId.of("3")),
                        index.byAllTags(Tag.of("oop"), Tag.of("java"))),
                () -> assertEquals(Set.of(CourseId.of("3")),
                        index.byAllTags(Tag.of("java"), Tag.of("web"))),
                () -> assertEquals(Set.of(CourseId.of("1"), CourseId.of("2"), CourseId.of("3")),
                        index.byAllTags(Tag.of("java")))
        );
    }

    @Test
    @DisplayName("Неизвестный тег даёт пустой результат")
    void unknownTagGivesEmptyResult() {
        TagIndex index = indexWithThreeCourses();

        assertAll(
                () -> assertEquals(Set.of(), index.byTag(Tag.of("unknown"))),
                () -> assertEquals(Set.of(), index.byAllTags(Tag.of("java"), Tag.of("unknown"))),
                () -> assertEquals(Set.of(), index.byAllTags())
        );
    }

    @Test
    @DisplayName("После remove id исчезает из byTag и пустой тег удаляется")
    void removeDropsIdAndEmptyTag() {
        TagIndex index = indexWithThreeCourses();
        Course removed = course("3", "Web", "java", "oop", "web");

        index.remove(removed);

        assertAll(
                () -> assertFalse(index.byTag(Tag.of("java")).contains(CourseId.of("3"))),
                () -> assertEquals(List.of(CourseId.of("1")), new ArrayList<>(index.byTag(Tag.of("oop")))),
                () -> assertEquals(Set.of(), index.byTag(Tag.of("web"))),
                () -> assertFalse(index.tags().contains(Tag.of("web"))),
                () -> assertEquals(2, index.size())
        );
    }

    @Test
    @DisplayName("Повторное добавление курса не дублирует id")
    void duplicateAddDoesNotDuplicateId() {
        TagIndex index = new TagIndex();

        index.add(course("1", "Java", "java"));
        index.add(course("1", "Java", "java"));
        index.add(course("1", "Java again", "java"));

        assertAll(
                () -> assertEquals(List.of(CourseId.of("1")), new ArrayList<>(index.byTag(Tag.of("java")))),
                () -> assertEquals(1, index.size())
        );
    }

    @Test
    @DisplayName("byTag возвращает снимок, а не живое представление")
    void byTagReturnsSnapshot() {
        TagIndex index = new TagIndex();
        index.add(course("1", "Java", "java"));
        Set<CourseId> snapshot = index.byTag(Tag.of("java"));

        index.add(course("2", "Algorithms", "java"));

        assertAll(
                () -> assertEquals(Set.of(CourseId.of("1")), snapshot),
                () -> assertTrue(index.byTag(Tag.of("java")).contains(CourseId.of("2")))
        );
    }
}
