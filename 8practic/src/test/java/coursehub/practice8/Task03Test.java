package coursehub.practice8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task03Test {

    @Test
    @DisplayName("reduce с правильной identity суммирует длительности")
    void correctIdentityKeepsResult() {
        List<Course> courses = List.of(
                new Course("C-1", "Alpha", 1, 60),
                new Course("C-2", "Beta", 2, 90),
                new Course("C-3", "Gamma", 1, 30));

        assertEquals(new Task3Totals.Totals(180, 3), Task3Totals.sum(courses));
        assertEquals(new Task3Totals.Totals(180, 3),
                Task3Totals.sumParallel(courses, Task3Totals.identity()));
    }

    @Test
    @DisplayName("неправильная identity меняет результат")
    void wrongIdentityChangesResult() {
        List<Course> courses = List.of(
                new Course("C-1", "Alpha", 1, 60),
                new Course("C-2", "Beta", 2, 90));

        Task3Totals.Totals expected = new Task3Totals.Totals(150, 2);
        Task3Totals.Totals wrong =
                Task3Totals.sum(courses, Task3Totals.wrongIdentity());

        assertEquals(new Task3Totals.Totals(160, 3), wrong);
        assertNotEquals(expected, wrong);
    }

    @Test
    @DisplayName("plus ассоциативен, identity нейтральна")
    void plusIsAssociativeAndIdentityIsNeutral() {
        Task3Totals.Totals a = new Task3Totals.Totals(10, 1);
        Task3Totals.Totals b = new Task3Totals.Totals(20, 2);
        Task3Totals.Totals c = new Task3Totals.Totals(30, 3);

        assertEquals(a.plus(b).plus(c), a.plus(b.plus(c)));
        assertEquals(a, a.plus(Task3Totals.identity()));
        assertEquals(a, Task3Totals.identity().plus(a));
    }
}
