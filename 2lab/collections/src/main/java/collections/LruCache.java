package collections;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class LruCache<K, V> {

    private static final class LruMap<K, V> extends LinkedHashMap<K, V> {

        private static final long serialVersionUID = 1L;

        private final int limit;

        LruMap(int limit) {
            super(limit, 0.75f, true);
            this.limit = limit;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > limit;
        }
    }

    private final int capacity;
    private final Map<K, V> entries;

    public LruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.entries = new LruMap<>(capacity);
    }

    public V get(K key) {
        Objects.requireNonNull(key, "key");
        return entries.get(key);
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        entries.put(key, value);
    }

    public boolean containsKey(K key) {
        Objects.requireNonNull(key, "key");
        return entries.containsKey(key);
    }

    public int size() {
        return entries.size();
    }

    public int capacity() {
        return capacity;
    }

    public List<K> keys() {
        return List.copyOf(entries.keySet());
    }
}
