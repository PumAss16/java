package coursehub.practice9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task02Test {

    @Test
    @Timeout(10)
    @DisplayName("volatile flag stops the worker")
    void volatileFlagStopsWorker() throws Exception {
        Task2StopFlag.VolatileWorker worker = new Task2StopFlag.VolatileWorker();
        Thread thread = new Thread(worker, "worker");
        thread.start();
        assertTrue(worker.awaitStarted());
        worker.stop();
        thread.join(2000);
        assertFalse(thread.isAlive());
        assertTrue(worker.processed() >= 1);
    }

    @Test
    @DisplayName("volatile does not make read-modify-write atomic")
    void volatileIncrementStillLoses() throws Exception {
        for (int round = 0; round < 20; round++) {
            assertEquals(1, Task2StopFlag.interleavedVolatileIncrement());
        }
    }
}
