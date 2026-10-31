package coursehub.practice8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task01Test {

    @Test
    @DisplayName("порядок не зависит от исходного порядка")
    void orderDoesNotDependOnInputOrder() {
        Course c1 = new Course("C-1", "Alpha", 2, 60);
        Course c2 = new Course("C-2", "Beta", 1, 90);
        Course c3 = new Course("C-3", "Gamma", 1, 30);
        Course c4 = new Course("C-4", "Delta", 1, 30);

        List<Course> expected = List.of(c3, c4, c2, c1);

        assertEquals(expected, Task1Sorter.sort(List.of(c1, c2, c3, c4)));
        assertEquals(expected, Task1Sorter.sort(List.of(c4, c3, c2, c1)));
        assertEquals(expected, Task1Sorter.sort(List.of(c2, c4, c1, c3)));
    }

    @Test
    @DisplayName("результат сортировки неизменяемый")
    void sortedResultIsImmutable() {
        List<Course> sorted = Task1Sorter.sort(List.of(
                new Course("C-1", "Alpha", 1, 30)));

        assertThrows(UnsupportedOperationException.class,
                () -> sorted.add(new Course("C-2", "Beta", 1, 60)));
    }
}
