package coursehub.practice9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task01Test {

    private static final int THREADS = 8;
    private static final int PER_THREAD = 50_000;
    private static final int EXPECTED = THREADS * PER_THREAD;

    @Test
    @DisplayName("interleaved read-compute-write loses an update in every run")
    void interleavedIncrementLoses() throws Exception {
        for (int round = 0; round < 20; round++) {
            assertEquals(1, Task1Counter.interleavedIncrement());
        }
    }

    @Test
    @Timeout(30)
    @DisplayName("synchronized method keeps the invariant")
    void synchronizedMethodIsExact() throws Exception {
        int value = Task1Counter.stress(new Task1Counter.SynchronizedCounter(), THREADS, PER_THREAD);
        assertEquals(EXPECTED, value);
    }

    @Test
    @Timeout(30)
    @DisplayName("synchronized block keeps the invariant")
    void synchronizedBlockIsExact() throws Exception {
        int value = Task1Counter.stress(new Task1Counter.BlockCounter(), THREADS, PER_THREAD);
        assertEquals(EXPECTED, value);
    }

    @Test
    @Timeout(30)
    @DisplayName("plain counter never exceeds the expected total")
    void plainCounterNeverExceeds() throws Exception {
        int value = Task1Counter.stress(new Task1Counter.PlainCounter(), THREADS, PER_THREAD);
        assertTrue(value <= EXPECTED);
    }
}
