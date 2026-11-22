package coursehub.practice9;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class Task4Pipeline {

    private Task4Pipeline() {
    }

    public record Request(long id, String studentId, String courseCode) {
    }

    public record Stats(int submitted, int processed, int maxQueueSize, boolean completed) {
    }

    private static final Request POISON = new Request(-1, "poison", "poison");

    public static Stats run(int producers, int consumers, int capacity, int perProducer, long timeoutMillis) {
        BlockingQueue<Request> queue = new ArrayBlockingQueue<>(capacity);
        AtomicInteger submitted = new AtomicInteger();
        AtomicInteger processed = new AtomicInteger();
        AtomicInteger maxQueue = new AtomicInteger();
        Set<Long> seen = ConcurrentHashMap.newKeySet();
        CountDownLatch producersDone = new CountDownLatch(producers);
        ExecutorService pool = Executors.newFixedThreadPool(producers + consumers, namedThreads("pipeline"));
        boolean completed = false;
        try {
            for (int p = 0; p < producers; p++) {
                int producerId = p;
                pool.execute(() -> {
                    try {
                        for (int i = 0; i < perProducer; i++) {
                            Request request = new Request(submitted.incrementAndGet(), "S-" + producerId, "C-" + i);
                            queue.put(request);
                            maxQueue.accumulateAndGet(queue.size(), Math::max);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        producersDone.countDown();
                    }
                });
            }
            for (int c = 0; c < consumers; c++) {
                pool.execute(() -> {
                    while (true) {
                        Request request;
                        try {
                            request = queue.take();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        if (request == POISON) {
                            return;
                        }
                        if (seen.add(request.id())) {
                            processed.incrementAndGet();
                        }
                    }
                });
            }
            if (producersDone.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
                for (int c = 0; c < consumers; c++) {
                    queue.put(POISON);
                }
                pool.shutdown();
                completed = pool.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
                if (!completed) {
                    pool.shutdownNow();
                }
            } else {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pool.shutdownNow();
        } finally {
            if (!pool.isShutdown()) {
                pool.shutdownNow();
            }
        }
        return new Stats(submitted.get(), processed.get(), maxQueue.get(), completed);
    }

    public static int suggestedPoolSize(boolean cpuBound) {
        int cores = Runtime.getRuntime().availableProcessors();
        return cpuBound ? cores : cores * 2;
    }

    public static ThreadFactory namedThreads(String prefix) {
        AtomicInteger sequence = new AtomicInteger();
        return runnable -> new Thread(runnable, prefix + "-" + sequence.incrementAndGet());
    }
}
