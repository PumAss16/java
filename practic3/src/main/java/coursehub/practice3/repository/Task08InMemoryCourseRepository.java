package coursehub.practice3.repository;

import coursehub.practice3.domain.Task00Course;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class Task08InMemoryCourseRepository implements Task08CourseRepository {

    private final Map<String, Task00Course> courses = new LinkedHashMap<>();

    @Override
    public Optional<Task00Course> findById(String code) {
        Objects.requireNonNull(code, "code");
        return Optional.ofNullable(courses.get(code));
    }

    @Override
    public List<Task00Course> findAll() {
        return List.copyOf(courses.values());
    }

    @Override
    public void save(Task00Course course) {
        Objects.requireNonNull(course, "course");
        courses.put(course.code(), course);
    }

    @Override
    public boolean deleteById(String code) {
        Objects.requireNonNull(code, "code");
        return courses.remove(code) != null;
    }
}
