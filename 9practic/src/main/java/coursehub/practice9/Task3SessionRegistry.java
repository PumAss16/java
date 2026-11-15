package coursehub.practice9;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class Task3SessionRegistry {

    public record Session(String id, String studentId) {
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Optional<Session> createIfAbsent(Session session) {
        Session existing = sessions.putIfAbsent(session.id(), session);
        return existing == null ? Optional.of(session) : Optional.empty();
    }

    public Optional<Session> computeIfAbsent(String id, String studentId) {
        return Optional.ofNullable(sessions.computeIfAbsent(id, key -> new Session(key, studentId)));
    }

    public Optional<Session> get(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public boolean replace(String id, Session expected, Session updated) {
        return sessions.replace(id, expected, updated);
    }

    public boolean remove(String id) {
        return sessions.remove(id) != null;
    }

    public int size() {
        return sessions.size();
    }
}
