package collections;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public final class Generics {

    private Generics() {
    }

    public static <T> T firstOrThrow(List<T> items) {
        if (items == null || items.isEmpty()) {
            throw new NoSuchElementException("empty list");
        }
        return items.get(0);
    }

    public static <T> void swap(List<T> items, int i, int j) {
        if (i < 0 || i >= items.size()) {
            throw new IndexOutOfBoundsException("index: " + i + ", size: " + items.size());
        }
        if (j < 0 || j >= items.size()) {
            throw new IndexOutOfBoundsException("index: " + j + ", size: " + items.size());
        }
        T tmp = items.get(i);
        items.set(i, items.get(j));
        items.set(j, tmp);
    }

    public static <T, K> Map<K, T> indexBy(List<T> items, Function<T, K> key) {
        Map<K, T> indexed = new LinkedHashMap<>();
        for (T item : items) {
            K k = key.apply(item);
            if (indexed.containsKey(k)) {
                throw new IllegalStateException("duplicate key: " + k);
            }
            indexed.put(k, item);
        }
        return indexed;
    }

    public static <T, K> Map<K, T> indexBy(List<T> items, Function<T, K> key, BinaryOperator<T> merge) {
        Map<K, T> indexed = new LinkedHashMap<>();
        for (T item : items) {
            indexed.merge(key.apply(item), item, merge);
        }
        return indexed;
    }

    public static <T extends Comparable<? super T>> T max(List<? extends T> items) {
        if (items == null || items.isEmpty()) {
            throw new NoSuchElementException("empty list");
        }
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    public static <T> void copy(List<? extends T> src, List<? super T> dst) {
        for (T item : src) {
            dst.add(item);
        }
    }
}
