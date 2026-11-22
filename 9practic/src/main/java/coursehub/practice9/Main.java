package coursehub.practice9;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        int expected = 8 * 100_000;
        System.out.println("task1 expected=" + expected
                + " plain=" + Task1Counter.stress(new Task1Counter.PlainCounter(), 8, 100_000)
                + " synchronized=" + Task1Counter.stress(new Task1Counter.SynchronizedCounter(), 8, 100_000)
                + " block=" + Task1Counter.stress(new Task1Counter.BlockCounter(), 8, 100_000));
        System.out.println("task1 interleaved=" + Task1Counter.interleavedIncrement());

        Task2StopFlag.VolatileWorker worker = new Task2StopFlag.VolatileWorker();
        Thread workerThread = new Thread(worker, "worker");
        workerThread.start();
        worker.awaitStarted();
        worker.stop();
        workerThread.join(2000);
        System.out.println("task2 stopped=" + !workerThread.isAlive()
                + " volatileInterleaved=" + Task2StopFlag.interleavedVolatileIncrement());

        Task3SessionRegistry registry = new Task3SessionRegistry();
        Task3SessionRegistry.Session session = new Task3SessionRegistry.Session("s-1", "S-1");
        System.out.println("task3 first=" + registry.createIfAbsent(session).isPresent()
                + " second=" + registry.createIfAbsent(session).isPresent()
                + " size=" + registry.size());

        System.out.println("task4 " + Task4Pipeline.run(3, 3, 4, 200, 10_000));

        Task3SessionRegistry raced = new Task3SessionRegistry();
        boolean finished = Task5Race.runConcurrently("main-race", 8,
                () -> raced.createIfAbsent(new Task3SessionRegistry.Session("s-1", "S-1")));
        System.out.println("task5 finished=" + finished + " size=" + raced.size());

        List<Task6Enrollment.Enrollment> variants = List.of(
                new Task6Enrollment.SynchronizedEnrollment(10),
                new Task6Enrollment.ConcurrentEnrollment(10),
                new Task6Enrollment.CasEnrollment(10));
        for (Task6Enrollment.Enrollment enrollment : variants) {
            AtomicInteger next = new AtomicInteger();
            Task5Race.runConcurrently("main-enroll", 32, () -> enrollment.enroll("S-" + next.getAndIncrement()));
            System.out.println("task6 " + enrollment.getClass().getSimpleName()
                    + " size=" + enrollment.size() + "/" + enrollment.capacity());
        }
    }
}
