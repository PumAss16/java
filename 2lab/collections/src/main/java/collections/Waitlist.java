package collections;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Waitlist {

    private final Deque<CourseId> queue = new ArrayDeque<>();

    public boolean add(CourseId id) {
        return queue.add(id);
    }

    public boolean offer(CourseId id) {
        return queue.offer(id);
    }

    public CourseId poll() {
        return queue.poll();
    }

    public CourseId peek() {
        return queue.peek();
    }

    public CourseId removeNext() {
        return queue.remove();
    }

    public CourseId element() {
        return queue.element();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
