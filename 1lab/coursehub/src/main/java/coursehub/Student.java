package coursehub;

import java.util.Set;

/** Студент и завершённые им курсы (нужны для проверки prerequisite). */
public class Student {

    private final String id;
    private final Set<CourseCode> completed;

    public Student(String id, CourseCode... completed) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("student id is required");
        }
        this.id = id;
        this.completed = Set.of(completed);
    }

    public String id() {
        return id;
    }

    public boolean hasCompleted(CourseCode code) {
        return completed.contains(code);
    }
}
