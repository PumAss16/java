package coursehub.practice10;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Task6Deadline {

    private Task6Deadline() {
    }

    public static final class DeadlineExceededException extends Exception {
        public DeadlineExceededException(String message) {
            super(message);
        }
    }

    public static final class Deadline {
        private final long startNanos;
        private final long budgetNanos;

        public Deadline(Duration budget) {
            this.startNanos = System.nanoTime();
            this.budgetNanos = budget.toNanos();
        }

        public long remainingNanos() {
            return Math.max(0, budgetNanos - (System.nanoTime() - startNanos));
        }

        public long remainingMillis() {
            return TimeUnit.NANOSECONDS.toMillis(remainingNanos());
        }

        public boolean expired() {
            return remainingNanos() == 0;
        }

        public <T> T await(Future<T> future, String stage)
                throws DeadlineExceededException, ExecutionException, InterruptedException {
            long remaining = remainingNanos();
            if (remaining == 0) {
                throw new DeadlineExceededException(stage + ": deadline already expired");
            }
            try {
                return future.get(remaining, TimeUnit.NANOSECONDS);
            } catch (TimeoutException e) {
                throw new DeadlineExceededException(stage + ": budget exhausted");
            }
        }
    }

    public static List<String> run(long budgetMillis, long... stageMillis) throws Exception {
        if (stageMillis.length == 0) {
            return List.of();
        }
        Deadline deadline = new Deadline(Duration.ofMillis(budgetMillis));
        ExecutorService pool = Executors.newFixedThreadPool(stageMillis.length, Task4Executors.namedThreads("stage"));
        List<Future<String>> futures = new ArrayList<>();
        try {
            for (int index = 0; index < stageMillis.length; index++) {
                int stage = index;
                long duration = stageMillis[index];
                futures.add(pool.submit(() -> {
                    Thread.sleep(duration);
                    return "stage-" + stage + " done";
                }));
            }
            List<String> results = new ArrayList<>();
            for (int index = 0; index < futures.size(); index++) {
                try {
                    results.add(deadline.await(futures.get(index), "stage-" + index));
                } catch (DeadlineExceededException e) {
                    futures.forEach(future -> future.cancel(true));
                    throw e;
                }
            }
            return results;
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(2, TimeUnit.SECONDS);
        }
    }
}
