package coursehub.practice10;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public final class Task4Executors {

    private Task4Executors() {
    }

    public record ThreadObservation(String name, boolean virtual) {
    }

    public static ExecutorService cpuExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(cores, namedThreads("cpu"));
    }

    public static ExecutorService ioExecutor() {
        return Executors.newFixedThreadPool(64, namedThreads("io"));
    }

    public static ExecutorService virtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    public static int poolSize(ExecutorService executor) {
        if (executor instanceof ThreadPoolExecutor pool) {
            return pool.getMaximumPoolSize();
        }
        return -1;
    }

    public static String starvationDemo() {
        ExecutorService single = Executors.newSingleThreadExecutor(namedThreads("single"));
        try {
            Future<String> outer = single.submit(() -> {
                Future<String> inner = single.submit(() -> "inner");
                try {
                    return inner.get(500, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    return "STARVED";
                }
            });
            return outer.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "INTERRUPTED";
        } catch (ExecutionException e) {
            return "FAILED";
        } catch (TimeoutException e) {
            return "TIMEOUT";
        } finally {
            single.shutdownNow();
        }
    }

    public static long cpuSum(int tasks, int limit) {
        ExecutorService pool = cpuExecutor();
        try {
            List<Callable<Long>> jobs = new ArrayList<>();
            for (int task = 0; task < tasks; task++) {
                int offset = task;
                jobs.add(() -> {
                    long sum = 0;
                    for (int number = 0; number <= limit; number++) {
                        sum += number;
                    }
                    return sum + offset;
                });
            }
            long total = 0;
            for (Future<Long> future : pool.invokeAll(jobs)) {
                total += future.get();
            }
            return total;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        } finally {
            pool.shutdownNow();
        }
    }

    public static List<ThreadObservation> observe(ExecutorService executor, int tasks, long sleepMillis) {
        List<Callable<ThreadObservation>> jobs = new ArrayList<>();
        for (int task = 0; task < tasks; task++) {
            jobs.add(() -> {
                Thread.sleep(sleepMillis);
                Thread thread = Thread.currentThread();
                return new ThreadObservation(thread.toString(), thread.isVirtual());
            });
        }
        try {
            List<ThreadObservation> observations = new ArrayList<>();
            for (Future<ThreadObservation> future : executor.invokeAll(jobs)) {
                observations.add(future.get());
            }
            return observations;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    public static ThreadFactory namedThreads(String prefix) {
        AtomicInteger sequence = new AtomicInteger();
        return runnable -> new Thread(runnable, prefix + "-" + sequence.incrementAndGet());
    }
}
