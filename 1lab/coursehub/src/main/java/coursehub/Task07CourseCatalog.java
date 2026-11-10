package coursehub;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Task07CourseCatalog {

    private final Map<Task10CourseCode, Task00Course> byCode = new HashMap<>();

    public void add(Task00Course course) {
        byCode.put(course.code(), course);
    }

    public Optional<Task00Course> findCourse(Task10CourseCode code) {
        return Optional.ofNullable(byCode.get(code));
    }
}
