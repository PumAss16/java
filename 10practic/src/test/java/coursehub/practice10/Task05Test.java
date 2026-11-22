package coursehub.practice10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task05Test {

    @Test
    @Timeout(60)
    @DisplayName("platform run is bounded by the pool size")
    void platformRunUsesFewThreads() {
        Task5Experiment.Result result = Task5Experiment.runPlatform(100, 20);

        assertEquals("platform", result.kind());
        assertEquals(100, result.tasks());
        assertTrue(result.platformThreads() > 0);
        assertTrue(result.platformThreads() <= 8);
        assertEquals(0, result.virtualThreads());
    }

    @Test
    @Timeout(60)
    @DisplayName("virtual run uses one virtual thread per task")
    void virtualRunUsesVirtualThreads() {
        Task5Experiment.Result result = Task5Experiment.runVirtual(100, 20);

        assertEquals(100, result.virtualThreads());
        assertEquals(0, result.platformThreads());
    }

    @Test
    @Timeout(60)
    @DisplayName("waiting on virtual threads is faster than a small platform pool")
    void virtualWaitingIsFaster() {
        Task5Experiment.Result platform = Task5Experiment.runPlatform(200, 20);
        Task5Experiment.Result virtual = Task5Experiment.runVirtual(200, 20);

        assertTrue(virtual.durationMillis() < platform.durationMillis(),
                "virtual=" + virtual.durationMillis() + " ms, platform=" + platform.durationMillis() + " ms");
    }
}
