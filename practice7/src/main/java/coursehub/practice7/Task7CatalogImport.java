package coursehub.practice7;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class Task7CatalogImport {

    private Task7CatalogImport() {
    }

    public sealed interface ImportResult permits Success, Failure {
    }

    public record Success(List<Course> courses) implements ImportResult {

        public Success {
            courses = List.copyOf(courses);
        }
    }

    public record Failure(List<ImportError> errors) implements ImportResult {

        public Failure {
            errors = List.copyOf(errors);
            if (errors.isEmpty()) {
                throw new IllegalArgumentException("failure must contain at least one error");
            }
        }
    }

    public static ImportResult parse(String text) {
        Objects.requireNonNull(text, "text");
        return parseLines(text.lines().toList());
    }

    public static ImportResult importFile(Path file) throws IOException {
        Objects.requireNonNull(file, "file");
        return parseLines(Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    public static ImportResult importAndPublish(Path file, Consumer<List<Course>> publisher)
            throws IOException {
        Objects.requireNonNull(publisher, "publisher");
        ImportResult result = importFile(file);
        if (result instanceof Success success) {
            publisher.accept(success.courses());
        }
        return result;
    }

    private static ImportResult parseLines(List<String> lines) {
        List<Course> courses = new ArrayList<>();
        List<ImportError> errors = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();
        long lineNumber = 0;

        for (String line : lines) {
            lineNumber++;
            CatalogLineParser.ParsedLine parsed = CatalogLineParser.parse(line);
            if (!parsed.valid()) {
                errors.add(new ImportError(lineNumber, parsed.error()));
                continue;
            }
            Course course = parsed.course();
            if (!seenCodes.add(course.code())) {
                errors.add(new ImportError(lineNumber, "duplicate course code: " + course.code()));
                continue;
            }
            courses.add(course);
        }

        if (errors.isEmpty()) {
            return new Success(courses);
        }
        return new Failure(errors);
    }
}
