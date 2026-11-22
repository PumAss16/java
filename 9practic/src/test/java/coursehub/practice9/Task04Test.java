package coursehub.practice9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task04Test {

    @Test
    @Timeout(30)
    @DisplayName("pipeline processes every request exactly once")
    void processesEveryRequest() {
        Task4Pipeline.Stats stats = Task4Pipeline.run(3, 3, 4, 200, 10_000);

        assertTrue(stats.completed());
        assertEquals(600, stats.submitted());
        assertEquals(600, stats.processed());
        assertTrue(stats.maxQueueSize() <= 4);
    }

    @Test
    @Timeout(30)
    @DisplayName("bounded queue never grows past capacity")
    void queueRespectsCapacity() {
        Task4Pipeline.Stats stats = Task4Pipeline.run(4, 1, 2, 100, 10_000);

        assertTrue(stats.completed());
        assertEquals(400, stats.processed());
        assertTrue(stats.maxQueueSize() <= 2);
    }

    @Test
    @Timeout(30)
    @DisplayName("shutdown releases every pipeline thread")
    void shutdownReleasesThreads() {
        Task4Pipeline.run(2, 2, 2, 100, 10_000);

        long alive = Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().startsWith("pipeline-") && thread.isAlive())
                .count();
        assertEquals(0, alive);
    }

    @Test
    @DisplayName("pool size follows the workload")
    void poolSizeFollowsWorkload() {
        int cores = Runtime.getRuntime().availableProcessors();
        assertEquals(cores, Task4Pipeline.suggestedPoolSize(true));
        assertEquals(cores * 2, Task4Pipeline.suggestedPoolSize(false));
    }
}
