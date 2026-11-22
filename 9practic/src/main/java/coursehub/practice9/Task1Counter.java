package coursehub.practice9;

import java.util.concurrent.CountDownLatch;

public final class Task1Counter {

    private Task1Counter() {
    }

    public interface Counter {
        void increment();

        int value();
    }

    public static final class PlainCounter implements Counter {
        private int value;

        @Override
        public void increment() {
            value = value + 1;
        }

        @Override
        public int value() {
            return value;
        }
    }

    public static final class SynchronizedCounter implements Counter {
        private int value;

        @Override
        public synchronized void increment() {
            value++;
        }

        @Override
        public synchronized int value() {
            return value;
        }
    }

    public static final class BlockCounter implements Counter {
        private final Object monitor = new Object();
        private int value;

        @Override
        public void increment() {
            synchronized (monitor) {
                value++;
            }
        }

        @Override
        public int value() {
            synchronized (monitor) {
                return value;
            }
        }
    }

    public static int stress(Counter counter, int threads, int perThread) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            int index = i;
            workers[i] = new Thread(() -> {
                await(start);
                for (int j = 0; j < perThread; j++) {
                    counter.increment();
                }
            }, "stress-" + index);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
        return counter.value();
    }

    public static int interleavedIncrement() throws InterruptedException {
        int[] cell = {0};
        CountDownLatch read = new CountDownLatch(1);
        CountDownLatch write = new CountDownLatch(1);
        Thread slow = new Thread(() -> {
            int current = cell[0];
            read.countDown();
            await(write);
            cell[0] = current + 1;
        }, "slow-reader");
        Thread fast = new Thread(() -> {
            await(read);
            cell[0] = cell[0] + 1;
            write.countDown();
        }, "fast-writer");
        slow.start();
        fast.start();
        slow.join();
        fast.join();
        return cell[0];
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
