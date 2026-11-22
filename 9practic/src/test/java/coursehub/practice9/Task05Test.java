package coursehub.practice9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task05Test {

    @Test
    @Timeout(10)
    @DisplayName("barrier start makes the race reproducible")
    void barrierStartIsDeterministic() throws Exception {
        for (int round = 0; round < 30; round++) {
            Task3SessionRegistry registry = new Task3SessionRegistry();
            AtomicInteger winners = new AtomicInteger();
            AtomicInteger maxSize = new AtomicInteger();

            boolean finished = Task5Race.runConcurrently("barrier-race", 8, () -> {
                Task3SessionRegistry.Session session = new Task3SessionRegistry.Session("s-1", "S-1");
                if (registry.createIfAbsent(session).isPresent()) {
                    winners.incrementAndGet();
                }
                maxSize.accumulateAndGet(registry.size(), Math::max);
            });

            assertTrue(finished);
            assertEquals(1, winners.get());
            assertEquals(1, maxSize.get());
        }
    }

    @Test
    @Timeout(10)
    @DisplayName("executor is released after every race")
    void executorIsReleased() throws Exception {
        for (int round = 0; round < 10; round++) {
            AtomicInteger counter = new AtomicInteger();
            Task5Race.runConcurrently("release-race", 4, counter::incrementAndGet);
            assertEquals(4, counter.get());
        }

        long alive = Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().startsWith("release-race-") && thread.isAlive())
                .count();
        assertEquals(0, alive);
    }
}
