package coursehub;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Каталог курсов: поиск возвращает Optional, отсутствие — не ошибка (задание 7). */
public class CourseCatalog {

    private final Map<CourseCode, Course> byCode = new HashMap<>();

    public void add(Course course) {
        byCode.put(course.code(), course);
    }

    public Optional<Course> findCourse(CourseCode code) {
        return Optional.ofNullable(byCode.get(code));
    }
}
