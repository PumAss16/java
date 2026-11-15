package coursehub.practice9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class Task06Test {

    private static final int CAPACITY = 10;

    private static List<Task6Enrollment.Enrollment> variants() {
        return List.of(
                new Task6Enrollment.SynchronizedEnrollment(CAPACITY),
                new Task6Enrollment.ConcurrentEnrollment(CAPACITY),
                new Task6Enrollment.CasEnrollment(CAPACITY));
    }

    @Test
    @Timeout(20)
    @DisplayName("successful enrollments never exceed capacity")
    void neverExceedsCapacity() throws Exception {
        for (Task6Enrollment.Enrollment enrollment : variants()) {
            AtomicInteger successes = new AtomicInteger();
            AtomicInteger next = new AtomicInteger();

            boolean finished = Task5Race.runConcurrently("enroll", 32, () -> {
                if (enrollment.enroll("S-" + next.getAndIncrement())) {
                    successes.incrementAndGet();
                }
            });

            assertTrue(finished);
            assertTrue(successes.get() <= CAPACITY);
            assertEquals(enrollment.size(), successes.get());
            assertEquals(CAPACITY, enrollment.size());
        }
    }

    @Test
    @Timeout(20)
    @DisplayName("a student never takes two seats")
    void studentTakesOneSeat() throws Exception {
        for (Task6Enrollment.Enrollment enrollment : variants()) {
            AtomicInteger successes = new AtomicInteger();

            Task5Race.runConcurrently("duplicate", 16, () -> {
                if (enrollment.enroll("S-1")) {
                    successes.incrementAndGet();
                }
            });

            assertEquals(1, successes.get());
            assertEquals(1, enrollment.size());
        }
    }

    @Test
    @DisplayName("rejection leaves the state untouched")
    void rejectionKeepsState() {
        for (Task6Enrollment.Enrollment enrollment : variants()) {
            for (int i = 0; i < CAPACITY; i++) {
                assertTrue(enrollment.enroll("S-" + i));
            }
            int before = enrollment.size();

            assertFalse(enrollment.enroll("S-" + CAPACITY));
            assertFalse(enrollment.enroll("S-0"));

            assertEquals(before, enrollment.size());
            assertEquals(CAPACITY, enrollment.size());
        }
    }
}
