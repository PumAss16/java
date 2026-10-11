package collections;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class InMemoryRepository<ID, T> implements Repository<ID, T> {

    private final Map<ID, T> store = new LinkedHashMap<>();

    @Override
    public Optional<T> save(ID id, T entity) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(entity, "entity");
        return Optional.ofNullable(store.put(id, entity));
    }

    @Override
    public Optional<T> findById(ID id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public boolean deleteById(ID id) {
        Objects.requireNonNull(id, "id");
        return store.remove(id) != null;
    }

    @Override
    public int size() {
        return store.size();
    }
}
