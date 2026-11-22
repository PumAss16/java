package coursehub.practice10;

import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        task1();
        task2();
        task3();
        task4();
        task5();
        task6();
    }

    private static void task1() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2, Task4Executors.namedThreads("task1"));
        try {
            List<Long> values = List.of(10L, 20L, 30L, 40L);
            System.out.println("task1 callable=" + Task1Future.runCallable(pool, values)
                    + " runnable=" + Task1Future.runRunnable(pool, values));

            Task1Future.Outcome<Long> failed =
                    Task1Future.await(pool.submit(new Task1Future.FailingTask()), 1000);
            System.out.println("task1 failed=" + failed.status() + " cause=" + failed.error());

            Task1Future.LongTask longTask = new Task1Future.LongTask(5000);
            Future<Long> longFuture = pool.submit(longTask);
            Task1Future.Outcome<Long> timeout = Task1Future.await(longFuture, 100);
            awaitClosed(longTask);
            System.out.println("task1 timeout=" + timeout.status()
                    + " cancelled=" + longFuture.isCancelled()
                    + " closed=" + longTask.closed()
                    + " processed=" + longTask.processed());

            Task1Future.FlagTask flagTask = new Task1Future.FlagTask();
            Future<?> flagFuture = pool.submit(flagTask);
            Thread.sleep(50);
            flagFuture.cancel(true);
            awaitRestored(flagTask);
            System.out.println("task1 interruptRestored=" + flagTask.restored());
        } finally {
            pool.shutdownNow();
        }
    }

    private static void task2() throws Exception {
        ExecutorService pool = Task2Pipeline.newPool("card");
        try {
            Task2Pipeline.Trace trace = new Task2Pipeline.Trace();
            String card = Task2Pipeline.chain(pool, "JAVA-101", trace).get(5, TimeUnit.SECONDS);
            System.out.println("task2 chain=" + card);
            System.out.println("task2 stages=" + trace.entries());
            System.out.println("task2 card="
                    + Task2Pipeline.loadCard(pool, "JAVA-101", "S-1").get(5, TimeUnit.SECONDS));
            System.out.println("task2 nested=" + Task2Pipeline.loadEnrollmentsNested(pool, "S-1")
                    .thenApply(inner -> inner.getClass().getSimpleName())
                    .get(5, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
        }
    }

    private static void task3() {
        System.out.println("task3 order=" + Task3AllOf.partial(List.of("C-1", "C-2", "C-3")).join().values());
        System.out.println("task3 partial=" + Task3AllOf.partial(List.of("C-1", "bad-2", "C-3")).join());
        try {
            Task3AllOf.failFast(List.of("bad-1", "C-2", "C-3")).join();
            System.out.println("task3 failFast=unexpected success");
        } catch (CompletionException e) {
            System.out.println("task3 failFast=" + e.getCause().getMessage());
        }
    }

    private static void task4() throws Exception {
        System.out.println("task4 starvation=" + Task4Executors.starvationDemo());
        System.out.println("task4 cpuSum=" + Task4Executors.cpuSum(4, 1000));

        ExecutorService cpu = Task4Executors.cpuExecutor();
        ExecutorService io = Task4Executors.ioExecutor();
        ExecutorService virtual = Task4Executors.virtualExecutor();
        try {
            System.out.println("task4 cpuPool=" + Task4Executors.poolSize(cpu)
                    + " ioPool=" + Task4Executors.poolSize(io));
            List<Task4Executors.ThreadObservation> ioThreads = Task4Executors.observe(io, 8, 50);
            List<Task4Executors.ThreadObservation> virtualThreads = Task4Executors.observe(virtual, 8, 50);
            System.out.println("task4 ioThreadCount=" + ioThreads.stream()
                    .map(Task4Executors.ThreadObservation::name).distinct().count()
                    + " virtualThreadCount=" + virtualThreads.stream()
                    .map(Task4Executors.ThreadObservation::name).distinct().count()
                    + " virtual=" + virtualThreads.get(0).virtual());
        } finally {
            cpu.shutdownNow();
            io.shutdownNow();
            virtual.shutdownNow();
        }
    }

    private static void task5() {
        Task5Experiment.Result platform = Task5Experiment.runPlatform(200, 20);
        Task5Experiment.Result virtual = Task5Experiment.runVirtual(200, 20);
        System.out.println("task5 platform=" + platform);
        System.out.println("task5 virtual=" + virtual);
    }

    private static void task6() throws Exception {
        System.out.println("task6 completed=" + Task6Deadline.run(1000, 30, 40, 50));
        try {
            System.out.println("task6 missed=" + Task6Deadline.run(150, 50, 500));
        } catch (Task6Deadline.DeadlineExceededException e) {
            System.out.println("task6 missed=" + e.getMessage());
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
