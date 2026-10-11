package collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task05Test {

    private static List<String> sample() {
        return new ArrayList<>(List.of("a", "b", "c", "d"));
    }

    @Test
    @DisplayName("Удаление в цикле for-each бросает ConcurrentModificationException")
    void removeInForEachThrowsConcurrentModificationException() {
        List<String> list = sample();

        assertThrows(ConcurrentModificationException.class, () -> {
            for (String value : list) {
                if (value.equals("b")) {
                    list.remove(value);
                }
            }
        });
    }

    @Test
    @DisplayName("Удаление внутри forEach бросает ConcurrentModificationException")
    void removeInsideForEachThrowsConcurrentModificationException() {
        List<String> list = sample();

        assertThrows(ConcurrentModificationException.class, () -> list.forEach(value -> {
            if (value.equals("b")) {
                list.remove(value);
            }
        }));
    }

    @Test
    @DisplayName("Iterator.remove удаляет элемент без исключения")
    void iteratorRemoveWorks() {
        List<String> list = sample();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals("b")) {
                iterator.remove();
            }
        }

        assertEquals(List.of("a", "c", "d"), list);
    }

    @Test
    @DisplayName("removeIf удаляет элемент без исключения")
    void removeIfWorks() {
        List<String> list = sample();

        list.removeIf(value -> value.equals("b"));

        assertEquals(List.of("a", "c", "d"), list);
    }

    @Test
    @DisplayName("Обход копии списка позволяет удалять из оригинала")
    void iterationOverCopyWorks() {
        List<String> list = sample();
        List<String> seen = new ArrayList<>();

        for (String value : List.copyOf(list)) {
            seen.add(value);
            if (value.equals("b")) {
                list.remove(value);
            }
        }

        assertAll(
                () -> assertEquals(List.of("a", "b", "c", "d"), seen),
                () -> assertEquals(List.of("a", "c", "d"), list)
        );
    }
}
