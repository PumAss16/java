package coursehub.practice3.repository;

import coursehub.practice3.domain.Task00Course;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class Task08ReadOnlyCourseRepository implements Task08CourseReader {

    private final Map<String, Task00Course> courses;

    public Task08ReadOnlyCourseRepository(List<Task00Course> courses) {
        Objects.requireNonNull(courses, "courses");
        Map<String, Task00Course> copy = new LinkedHashMap<>();
        for (Task00Course course : courses) {
            Objects.requireNonNull(course, "course");
            copy.put(course.code(), course);
        }
        this.courses = Collections.unmodifiableMap(copy);
    }

    @Override
    public Optional<Task00Course> findById(String code) {
        Objects.requireNonNull(code, "code");
        return Optional.ofNullable(courses.get(code));
    }

    @Override
    public List<Task00Course> findAll() {
        return List.copyOf(courses.values());
    }
}
