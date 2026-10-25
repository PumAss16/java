package coursehub.practice3.repository;

import coursehub.practice3.domain.Task00Course;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Task08CourseCatalogView {

    private final Task08CourseReader reader;

    public Task08CourseCatalogView(Task08CourseReader reader) {
        this.reader = Objects.requireNonNull(reader, "reader");
    }

    public String render() {
        return reader.findAll().stream()
                .map(Task00Course::title)
                .collect(Collectors.joining(", "));
    }
}
