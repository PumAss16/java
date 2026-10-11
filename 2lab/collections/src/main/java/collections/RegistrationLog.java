package collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RegistrationLog {

    private final List<String> storage;

    public RegistrationLog() {
        this.storage = new ArrayList<>();
    }

    public RegistrationLog(List<String> storage) {
        this.storage = storage;
    }

    public void add(String student) {
        storage.add(student);
    }

    public void insertAt(int index, String student) {
        storage.add(index, student);
    }

    public String removeAt(int index) {
        return storage.remove(index);
    }

    public List<String> order() {
        return List.copyOf(storage);
    }

    public int size() {
        return storage.size();
    }

    public List<String> duplicates() {
        Set<String> seen = new HashSet<>();
        Set<String> repeated = new LinkedHashSet<>();
        for (String student : storage) {
            if (!seen.add(student)) {
                repeated.add(student);
            }
        }
        return List.copyOf(repeated);
    }
}
