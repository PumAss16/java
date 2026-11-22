package coursehub.practice10;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Task5Experiment {

    private Task5Experiment() {
    }

    public record Result(String kind, int tasks, long sleepMillis, long durationMillis,
                         int platformThreads, int virtualThreads, int peakExtraThreads) {
    }

    public static Result runPlatform(int tasks, long sleepMillis) {
        ExecutorService pool = Executors.newFixedThreadPool(8, Task4Executors.namedThreads("exp-platform"));
        try {
            return run("platform", pool, tasks, sleepMillis);
        } finally {
            pool.shutdownNow();
        }
    }

    public static Result runVirtual(int tasks, long sleepMillis) {
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        try {
            return run("virtual", pool, tasks, sleepMillis);
        } finally {
            pool.shutdownNow();
        }
    }

    private static Result run(String kind, ExecutorService pool, int tasks, long sleepMillis) {
        Set<Thread> seen = ConcurrentHashMap.newKeySet();
        List<Callable<Void>> jobs = new ArrayList<>();
        for (int task = 0; task < tasks; task++) {
            jobs.add(() -> {
                seen.add(Thread.currentThread());
                Thread.sleep(sleepMillis);
                return null;
            });
        }
        AtomicInteger peak = new AtomicInteger();
        AtomicBoolean sampling = new AtomicBoolean(true);
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        int baseline = bean.getThreadCount();
        Thread sampler = new Thread(() -> {
            while (sampling.get()) {
                peak.accumulateAndGet(bean.getThreadCount() - baseline, Math::max);
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "thread-sampler");
        sampler.setDaemon(true);
        long start = System.nanoTime();
        sampler.start();
        try {
            for (Future<Void> future : pool.invokeAll(jobs)) {
                future.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        } finally {
            sampling.set(false);
            sampler.interrupt();
            try {
                sampler.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        int platform = (int) seen.stream().filter(thread -> !thread.isVirtual()).count();
        int virtual = seen.size() - platform;
        return new Result(kind, tasks, sleepMillis, duration, platform, virtual, peak.get());
    }
}
