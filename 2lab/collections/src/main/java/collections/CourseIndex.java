package collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class CourseIndex {

    private final Map<CourseId, Course> courses = new HashMap<>();

    public boolean add(Course course) {
        return courses.putIfAbsent(course.id(), course) == null;
    }

    public void addOrThrow(Course course) {
        if (!add(course)) {
            throw new DuplicateIdException(course.id());
        }
    }

    public Optional<Course> find(CourseId id) {
        return Optional.ofNullable(courses.get(id));
    }

    public boolean has(CourseId id) {
        return courses.containsKey(id);
    }

    public int size() {
        return courses.size();
    }

    public Course replace(Course course) {
        return courses.put(course.id(), course);
    }

    public boolean insertViaGet(Course course) {
        if (courses.get(course.id()) != null) {
            return false;
        }
        courses.put(course.id(), course);
        return true;
    }

    public boolean insertViaContainsKey(Course course) {
        if (courses.containsKey(course.id())) {
            return false;
        }
        courses.put(course.id(), course);
        return true;
    }

    public boolean insertViaPutIfAbsent(Course course) {
        return courses.putIfAbsent(course.id(), course) == null;
    }

    public boolean insertViaComputeIfAbsent(Course course) {
        if (courses.containsKey(course.id())) {
            return false;
        }
        courses.computeIfAbsent(course.id(), key -> course);
        return true;
    }

    public Course findOrCompute(CourseId id, Supplier<Course> factory) {
        return courses.computeIfAbsent(id, key -> factory.get());
    }
}
