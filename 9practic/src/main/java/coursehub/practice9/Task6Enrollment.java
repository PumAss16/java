package coursehub.practice9;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class Task6Enrollment {

    private Task6Enrollment() {
    }

    public interface Enrollment {
        boolean enroll(String studentId);

        int size();

        int capacity();
    }

    public static final class SynchronizedEnrollment implements Enrollment {
        private final int capacity;
        private final Set<String> students = new HashSet<>();

        public SynchronizedEnrollment(int capacity) {
            this.capacity = capacity;
        }

        @Override
        public synchronized boolean enroll(String studentId) {
            if (students.size() >= capacity || students.contains(studentId)) {
                return false;
            }
            return students.add(studentId);
        }

        @Override
        public synchronized int size() {
            return students.size();
        }

        @Override
        public int capacity() {
            return capacity;
        }
    }

    public static final class ConcurrentEnrollment implements Enrollment {
        private final int capacity;
        private final Set<String> students = ConcurrentHashMap.newKeySet();
        private final AtomicInteger seats = new AtomicInteger();

        public ConcurrentEnrollment(int capacity) {
            this.capacity = capacity;
        }

        @Override
        public boolean enroll(String studentId) {
            if (seats.get() >= capacity) {
                return false;
            }
            if (!students.add(studentId)) {
                return false;
            }
            if (seats.incrementAndGet() > capacity) {
                seats.decrementAndGet();
                students.remove(studentId);
                return false;
            }
            return true;
        }

        @Override
        public int size() {
            return students.size();
        }

        @Override
        public int capacity() {
            return capacity;
        }
    }

    public static final class CasEnrollment implements Enrollment {

        private record State(Set<String> students, int seats) {
        }

        private final int capacity;
        private final AtomicReference<State> state;

        public CasEnrollment(int capacity) {
            this.capacity = capacity;
            this.state = new AtomicReference<>(new State(Set.of(), 0));
        }

        @Override
        public boolean enroll(String studentId) {
            while (true) {
                State current = state.get();
                if (current.seats() >= capacity || current.students().contains(studentId)) {
                    return false;
                }
                Set<String> next = new HashSet<>(current.students());
                next.add(studentId);
                State updated = new State(Set.copyOf(next), current.seats() + 1);
                if (state.compareAndSet(current, updated)) {
                    return true;
                }
            }
        }

        @Override
        public int size() {
            return state.get().seats();
        }

        @Override
        public int capacity() {
            return capacity;
        }
    }
}
