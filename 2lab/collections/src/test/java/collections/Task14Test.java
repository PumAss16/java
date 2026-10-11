package collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task14Test {

    private static LruCache<String, Integer> filled(int capacity) {
        LruCache<String, Integer> cache = new LruCache<>(capacity);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        return cache;
    }

    @Test
    @DisplayName("вытесняется самый старый элемент")
    void evictsOldestEntry() {
        LruCache<String, Integer> cache = filled(3);
        assertEquals(List.of("a", "b", "c"), cache.keys());
        cache.put("d", 4);
        assertEquals(List.of("b", "c", "d"), cache.keys());
        assertFalse(cache.containsKey("a"));
        assertEquals(3, cache.size());
        assertEquals(4, cache.get("d"));
    }

    @Test
    @DisplayName("get обновляет недавность ключа")
    void getRefreshesRecency() {
        LruCache<String, Integer> cache = filled(3);
        assertEquals(1, cache.get("a"));
        cache.put("d", 4);
        assertTrue(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"));
        assertEquals(List.of("c", "a", "d"), cache.keys());
    }

    @Test
    @DisplayName("containsKey не обновляет недавность")
    void containsKeyDoesNotRefreshRecency() {
        LruCache<String, Integer> cache = filled(3);
        assertTrue(cache.containsKey("a"));
        cache.put("d", 4);
        assertFalse(cache.containsKey("a"));
        assertEquals(List.of("b", "c", "d"), cache.keys());
    }

    @Test
    @DisplayName("равные ключи образуют одну запись")
    void equalKeysAreSingleEntry() {
        LruCache<CourseId, String> cache = new LruCache<>(2);
        cache.put(CourseId.of("C-1"), "one");
        cache.put(CourseId.of("C-1"), "two");
        assertEquals(1, cache.size());
        assertEquals("two", cache.get(CourseId.of(" C-1 ")));
        assertEquals(List.of(CourseId.of("C-1")), cache.keys());
    }

    @Test
    @DisplayName("null-аргументы отклоняются с NullPointerException")
    void nullArgumentsAreRejected() {
        LruCache<String, Integer> cache = new LruCache<>(2);
        assertThrows(NullPointerException.class, () -> cache.put(null, 1));
        assertThrows(NullPointerException.class, () -> cache.put("a", null));
        assertThrows(NullPointerException.class, () -> cache.get(null));
        assertThrows(NullPointerException.class, () -> cache.containsKey(null));
    }

    @Test
    @DisplayName("неположительная ёмкость запрещена")
    void capacityMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new LruCache<String, Integer>(0));
        assertThrows(IllegalArgumentException.class, () -> new LruCache<String, Integer>(-1));
        assertEquals(5, new LruCache<String, Integer>(5).capacity());
    }
}
