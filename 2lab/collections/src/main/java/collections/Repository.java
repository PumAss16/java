package collections;

import java.util.List;
import java.util.Optional;

public interface Repository<ID, T> {

    Optional<T> save(ID id, T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);

    int size();
}
