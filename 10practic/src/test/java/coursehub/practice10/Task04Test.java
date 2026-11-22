package coursehub.practice10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task04Test {

    @Test
    @Timeout(30)
    @DisplayName("a single thread starves a task waiting for another task on the same pool")
    void singlePoolStarves() {
        assertEquals("STARVED", Task4Executors.starvationDemo());
    }

    @Test
    @Timeout(30)
    @DisplayName("CPU pool is sized by available processors")
    void cpuPoolMatchesProcessors() {
        ExecutorService pool = Task4Executors.cpuExecutor();
        try {
            assertEquals(Runtime.getRuntime().availableProcessors(), Task4Executors.poolSize(pool));
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("platform pool reuses a bounded number of threads")
    void platformPoolReusesThreads() {
        ExecutorService pool = Executors.newFixedThreadPool(4, Task4Executors.namedThreads("obs"));
        try {
            List<Task4Executors.ThreadObservation> observations = Task4Executors.observe(pool, 8, 20);

            assertEquals(8, observations.size());
            assertTrue(observations.stream().noneMatch(Task4Executors.ThreadObservation::virtual));
            assertEquals(4, observations.stream()
                    .map(Task4Executors.ThreadObservation::name).distinct().count());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("virtual executor gives one virtual thread per blocking task")
    void virtualExecutorUsesVirtualThreads() {
        ExecutorService pool = Task4Executors.virtualExecutor();
        try {
            List<Task4Executors.ThreadObservation> observations = Task4Executors.observe(pool, 8, 20);

            assertTrue(observations.stream().allMatch(Task4Executors.ThreadObservation::virtual));
            assertEquals(8, observations.stream()
                    .map(Task4Executors.ThreadObservation::name).distinct().count());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("CPU-bound sum is correct")
    void cpuSumIsCorrect() {
        assertEquals(4 * 500_500L + 6, Task4Executors.cpuSum(4, 1000));
    }
}
