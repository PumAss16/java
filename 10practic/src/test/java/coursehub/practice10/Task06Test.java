package coursehub.practice10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task06Test {

    @Test
    @Timeout(30)
    @DisplayName("stages share one budget and complete in order")
    void stagesCompleteInOrder() throws Exception {
        assertEquals(List.of("stage-0 done", "stage-1 done", "stage-2 done"),
                Task6Deadline.run(1000, 20, 20, 20));
    }

    @Test
    @Timeout(30)
    @DisplayName("a missed deadline cancels the remaining work")
    void missedDeadlineCancels() throws Exception {
        assertThrows(Task6Deadline.DeadlineExceededException.class,
                () -> Task6Deadline.run(200, 50, 500));

        long alive = Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().startsWith("stage-") && thread.isAlive())
                .count();
        assertEquals(0, alive);
    }

    @Test
    @Timeout(30)
    @DisplayName("an expired budget fails before waiting")
    void expiredBudgetFails() {
        Task6Deadline.Deadline deadline = new Task6Deadline.Deadline(Duration.ZERO);

        assertThrows(Task6Deadline.DeadlineExceededException.class,
                () -> deadline.await(CompletableFuture.completedFuture("x"), "stage-0"));
    }

    @Test
    @Timeout(30)
    @DisplayName("remaining budget shrinks with time")
    void remainingBudgetShrinks() throws Exception {
        Task6Deadline.Deadline deadline = new Task6Deadline.Deadline(Duration.ofMillis(1000));
        long before = deadline.remainingMillis();
        Thread.sleep(50);

        assertTrue(deadline.remainingMillis() < before);
    }
}
