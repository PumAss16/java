package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task01Test {

    @Test
    @DisplayName("Лог сохраняет порядок добавления, вставки и удаления")
    void keepsSequenceAfterAddInsertRemove() {
        RegistrationLog log = new RegistrationLog();
        log.add("a");
        log.add("b");
        log.add("c");
        log.insertAt(1, "x");
        assertEquals(List.of("a", "x", "b", "c"), log.order());
        assertEquals("b", log.removeAt(2));
        assertEquals(List.of("a", "x", "c"), log.order());
        assertEquals(3, log.size());
    }

    @Test
    @DisplayName("duplicates находит повторы в порядке первого повтора")
    void duplicatesKeepsFirstRepeatOrder() {
        RegistrationLog log = new RegistrationLog(new ArrayList<>(List.of("A", "B", "A", "C", "B")));
        assertEquals(List.of("A", "B"), log.duplicates());

        RegistrationLog unique = new RegistrationLog(new ArrayList<>(List.of("A", "B", "C")));
        assertTrue(unique.duplicates().isEmpty());
        assertEquals(3, unique.size());
    }

    @Test
    @DisplayName("Грубая оценка порядка величины: ArrayList против LinkedList")
    void comparesArrayListAndLinkedList() {
        int size = 20_000;
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }

        long start = System.nanoTime();
        long arrayListGetSum = 0;
        for (int i = 0; i < size; i++) {
            arrayListGetSum += arrayList.get(i);
        }
        long arrayListGetNanos = System.nanoTime() - start;

        start = System.nanoTime();
        long linkedListGetSum = 0;
        for (int i = 0; i < size; i++) {
            linkedListGetSum += linkedList.get(i);
        }
        long linkedListGetNanos = System.nanoTime() - start;

        start = System.nanoTime();
        long arrayListForEachSum = 0;
        for (int value : arrayList) {
            arrayListForEachSum += value;
        }
        long arrayListForEachNanos = System.nanoTime() - start;

        start = System.nanoTime();
        long linkedListForEachSum = 0;
        for (int value : linkedList) {
            linkedListForEachSum += value;
        }
        long linkedListForEachNanos = System.nanoTime() - start;

        System.out.printf("доступ по индексу ArrayList: %d нс, %.2f нс/операцию%n",
                arrayListGetNanos, (double) arrayListGetNanos / size);
        System.out.printf("доступ по индексу LinkedList: %d нс, %.2f нс/операцию%n",
                linkedListGetNanos, (double) linkedListGetNanos / size);
        System.out.printf("обход for-each ArrayList: %d нс, %.2f нс/операцию%n",
                arrayListForEachNanos, (double) arrayListForEachNanos / size);
        System.out.printf("обход for-each LinkedList: %d нс, %.2f нс/операцию%n",
                linkedListForEachNanos, (double) linkedListForEachNanos / size);

        long expected = (long) size * (size - 1) / 2;
        assertEquals(size, arrayList.size());
        assertEquals(size, linkedList.size());
        assertEquals(expected, arrayListGetSum);
        assertEquals(expected, linkedListGetSum);
        assertEquals(expected, arrayListForEachSum);
        assertEquals(expected, linkedListForEachSum);
    }
}
