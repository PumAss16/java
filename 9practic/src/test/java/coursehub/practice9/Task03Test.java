package coursehub.practice9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task03Test {

    @Test
    @Timeout(15)
    @DisplayName("putIfAbsent creates exactly one session under race")
    void onlyOneCreatorWins() throws Exception {
        for (int round = 0; round < 30; round++) {
            Task3SessionRegistry registry = new Task3SessionRegistry();
            AtomicInteger winners = new AtomicInteger();
            boolean finished = Task5Race.runConcurrently("registry", 8, () -> {
                Task3SessionRegistry.Session session = new Task3SessionRegistry.Session("s-1", "S-1");
                if (registry.createIfAbsent(session).isPresent()) {
                    winners.incrementAndGet();
                }
            });
            assertTrue(finished);
            assertEquals(1, winners.get());
            assertEquals(1, registry.size());
        }
    }

    @Test
    @DisplayName("createIfAbsent keeps the first session")
    void firstSessionIsKept() {
        Task3SessionRegistry registry = new Task3SessionRegistry();
        Task3SessionRegistry.Session first = new Task3SessionRegistry.Session("s-1", "S-1");
        Task3SessionRegistry.Session second = new Task3SessionRegistry.Session("s-1", "S-2");

        assertTrue(registry.createIfAbsent(first).isPresent());
        assertFalse(registry.createIfAbsent(second).isPresent());
        assertEquals(Optional.of(first), registry.get("s-1"));
    }

    @Test
    @DisplayName("remove and replace are atomic")
    void removeAndReplace() {
        Task3SessionRegistry registry = new Task3SessionRegistry();
        Task3SessionRegistry.Session session = new Task3SessionRegistry.Session("s-1", "S-1");
        Task3SessionRegistry.Session updated = new Task3SessionRegistry.Session("s-1", "S-9");

        registry.createIfAbsent(session);

        assertFalse(registry.replace("s-1", updated, session));
        assertTrue(registry.replace("s-1", session, updated));
        assertEquals(Optional.of(updated), registry.get("s-1"));
        assertTrue(registry.remove("s-1"));
        assertFalse(registry.remove("s-1"));
        assertEquals(0, registry.size());
    }
}
