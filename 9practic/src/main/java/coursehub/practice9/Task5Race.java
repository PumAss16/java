package coursehub.practice9;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class Task5Race {

    private Task5Race() {
    }

    public static boolean runConcurrently(String name, int threads, Runnable body) throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(threads);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        ExecutorService pool = Executors.newFixedThreadPool(threads, Task4Pipeline.namedThreads(name));
        try {
            for (int i = 0; i < threads; i++) {
                pool.execute(() -> {
                    try {
                        barrier.await(2, TimeUnit.SECONDS);
                        body.run();
                    } catch (Throwable error) {
                        failure.compareAndSet(null, error);
                    } finally {
                        done.countDown();
                    }
                });
            }
            boolean finished = done.await(5, TimeUnit.SECONDS);
            Throwable error = failure.get();
            if (error != null) {
                throw new IllegalStateException("race body failed", error);
            }
            return finished;
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(2, TimeUnit.SECONDS);
        }
    }
}
