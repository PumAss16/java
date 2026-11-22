package coursehub.practice9;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Task2StopFlag {

    private Task2StopFlag() {
    }

    public static final class VolatileWorker implements Runnable {
        private volatile boolean running = true;
        private final CountDownLatch started = new CountDownLatch(1);
        private long processed;

        public void stop() {
            running = false;
        }

        public boolean awaitStarted() throws InterruptedException {
            return started.await(2, TimeUnit.SECONDS);
        }

        public long processed() {
            return processed;
        }

        @Override
        public void run() {
            while (running) {
                processed++;
                if (processed == 1) {
                    started.countDown();
                }
            }
        }
    }

    public static final class PlainWorker implements Runnable {
        private boolean running = true;
        private long processed;

        public void stop() {
            running = false;
        }

        public long processed() {
            return processed;
        }

        @Override
        public void run() {
            while (running) {
                processed++;
            }
        }
    }

    public static final class VolatileCounter {
        private volatile int value;

        public void increment() {
            value = value + 1;
        }

        public void setValue(int newValue) {
            value = newValue;
        }

        public int value() {
            return value;
        }
    }

    public static int interleavedVolatileIncrement() throws InterruptedException {
        VolatileCounter counter = new VolatileCounter();
        CountDownLatch read = new CountDownLatch(1);
        CountDownLatch write = new CountDownLatch(1);
        Thread slow = new Thread(() -> {
            int current = counter.value();
            read.countDown();
            await(write);
            counter.setValue(current + 1);
        }, "volatile-slow");
        Thread fast = new Thread(() -> {
            await(read);
            counter.increment();
            write.countDown();
        }, "volatile-fast");
        slow.start();
        fast.start();
        slow.join();
        fast.join();
        return counter.value();
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
