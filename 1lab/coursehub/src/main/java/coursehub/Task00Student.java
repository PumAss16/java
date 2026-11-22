package coursehub;

import java.util.Set;

public class Task00Student {

    private final String id;
    private final Set<Task10CourseCode> completed;

    public Task00Student(String id, Task10CourseCode... completed) {
        if (id == null || id.isBlank()) {
            throw new Task02ValidationException("student id is required");
        }
        this.id = id;
        this.completed = Set.of(completed);
    }

    public String id() {
        return id;
    }

    public boolean hasCompleted(Task10CourseCode code) {
        return completed.contains(code);
    }
}
