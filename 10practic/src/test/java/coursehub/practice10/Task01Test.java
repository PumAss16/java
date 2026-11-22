package coursehub.practice10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task01Test {

    private static final List<Long> VALUES = List.of(10L, 20L, 30L, 40L);

    @Test
    @Timeout(30)
    @DisplayName("Callable and Runnable both return the sum")
    void callableAndRunnableReturnTheSameSum() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2, Task4Executors.namedThreads("t1"));
        try {
            assertEquals(100L, Task1Future.runCallable(pool, VALUES));
            assertEquals(100L, Task1Future.runRunnable(pool, VALUES));
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("timeout is reported as TIMEOUT and cancels the future")
    void timeoutIsNotAnError() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor(Task4Executors.namedThreads("t1"));
        Task1Future.LongTask task = new Task1Future.LongTask(5000);
        try {
            Future<Long> future = pool.submit(task);
            Task1Future.Outcome<Long> outcome = Task1Future.await(future, 100);

            assertEquals(Task1Future.Status.TIMEOUT, outcome.status());
            assertTrue(future.isCancelled());
            awaitClosed(task);
            assertTrue(task.closed());
            assertTrue(task.processed() < 5000);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("computation error keeps its cause")
    void computationErrorKeepsCause() {
        ExecutorService pool = Executors.newSingleThreadExecutor(Task4Executors.namedThreads("t1"));
        try {
            Task1Future.Outcome<Long> outcome =
                    Task1Future.await(pool.submit(new Task1Future.FailingTask()), 1000);

            assertEquals(Task1Future.Status.FAILED, outcome.status());
            assertInstanceOf(IllegalStateException.class, outcome.error());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("cancel(true) interrupts the task and releases its resource")
    void cancelReleasesResource() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor(Task4Executors.namedThreads("t1"));
        Task1Future.LongTask task = new Task1Future.LongTask(5000);
        try {
            Future<Long> future = pool.submit(task);
            Thread.sleep(100);

            assertTrue(future.cancel(true));
            awaitClosed(task);
            assertTrue(task.closed());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("a task that cannot throw restores the interrupt flag")
    void interruptFlagIsRestored() throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor(Task4Executors.namedThreads("t1"));
        Task1Future.FlagTask task = new Task1Future.FlagTask();
        try {
            Future<?> future = pool.submit(task);
            Thread.sleep(50);
            future.cancel(true);
            awaitRestored(task);

            assertTrue(task.restored());
        } finally {
            pool.shutdownNow();
        }
    }

    private static void awaitClosed(Task1Future.LongTask task) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (!task.closed() && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
    }

    private static void awaitRestored(Task1Future.FlagTask task) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (!task.restored() && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
    }
}
