package coursehub.practice7;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Task1TextCatalog {

    private Task1TextCatalog() {
    }

    public static void save(Path file, List<Course> courses) throws IOException {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(courses, "courses");

        StringBuilder text = new StringBuilder();
        for (Course course : courses) {
            text.append(course.textLine()).append('\n');
        }
        Files.writeString(file, text, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    public static LoadResult load(Path file) throws IOException {
        Objects.requireNonNull(file, "file");

        if (!Files.exists(file)) {
            return new LoadResult(false, List.of());
        }
        if (!Files.isRegularFile(file)) {
            throw new IOException("not a regular file: " + file);
        }

        String text = Files.readString(file, StandardCharsets.UTF_8);
        List<Course> courses = new ArrayList<>();
        long lineNumber = 0;
        for (String line : text.lines().toList()) {
            lineNumber++;
            CatalogLineParser.ParsedLine parsed = CatalogLineParser.parse(line);
            if (!parsed.valid()) {
                throw new CatalogFormatException("line " + lineNumber + ": " + parsed.error());
            }
            courses.add(parsed.course());
        }
        return new LoadResult(true, courses);
    }

    public record LoadResult(boolean found, List<Course> courses) {

        public LoadResult {
            courses = List.copyOf(courses);
        }
    }
}
