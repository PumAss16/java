package coursehub.practice10;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Task1Future {

    private Task1Future() {
    }

    public enum Status {
        OK, TIMEOUT, FAILED, INTERRUPTED
    }

    public record Outcome<T>(Status status, T value, Throwable error) {
    }

    public static final class SumTask implements Callable<Long> {
        private final List<Long> values;

        public SumTask(List<Long> values) {
            this.values = List.copyOf(values);
        }

        @Override
        public Long call() throws InterruptedException {
            long sum = 0;
            for (Long value : values) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("sum task interrupted");
                }
                sum += value;
            }
            return sum;
        }
    }

    public static final class SumRunnable implements Runnable {
        private final List<Long> values;
        private volatile long sum;

        public SumRunnable(List<Long> values) {
            this.values = List.copyOf(values);
        }

        @Override
        public void run() {
            long total = 0;
            for (Long value : values) {
                total += value;
            }
            sum = total;
        }

        public long sum() {
            return sum;
        }
    }

    public static final class FailingTask implements Callable<Long> {
        @Override
        public Long call() {
            throw new IllegalStateException("bad input");
        }
    }

    public static final class LongTask implements Callable<Long> {
        private final long steps;
        private final AtomicBoolean closed = new AtomicBoolean();
        private volatile long processed;

        public LongTask(long steps) {
            this.steps = steps;
        }

        @Override
        public Long call() throws InterruptedException {
            try {
                for (long step = 0; step < steps; step++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("cancelled at step " + step);
                    }
                    processed++;
                    Thread.sleep(1);
                }
                return processed;
            } finally {
                closed.set(true);
            }
        }

        public boolean closed() {
            return closed.get();
        }

        public long processed() {
            return processed;
        }
    }

    public static final class FlagTask implements Runnable {
        private volatile boolean restored;

        @Override
        public void run() {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                restored = Thread.currentThread().isInterrupted();
            }
        }

        public boolean restored() {
            return restored;
        }
    }

    public static <T> Outcome<T> await(Future<T> future, long timeoutMillis) {
        try {
            return new Outcome<>(Status.OK, future.get(timeoutMillis, TimeUnit.MILLISECONDS), null);
        } catch (TimeoutException e) {
            future.cancel(true);
            return new Outcome<>(Status.TIMEOUT, null, e);
        } catch (ExecutionException e) {
            return new Outcome<>(Status.FAILED, null, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            return new Outcome<>(Status.INTERRUPTED, null, e);
        }
    }

    public static long runCallable(ExecutorService pool, List<Long> values) throws Exception {
        Future<Long> future = pool.submit(new SumTask(values));
        return future.get(5, TimeUnit.SECONDS);
    }

    public static long runRunnable(ExecutorService pool, List<Long> values) throws Exception {
        SumRunnable task = new SumRunnable(values);
        pool.submit(task).get(5, TimeUnit.SECONDS);
        return task.sum();
    }
}
