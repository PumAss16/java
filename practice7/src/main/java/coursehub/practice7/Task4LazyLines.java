package coursehub.practice7;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class Task4LazyLines {

    private Task4LazyLines() {
    }

    public static long totalCharacters(Path file) throws IOException {
        Objects.requireNonNull(file, "file");
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.mapToLong(String::length).sum();
        }
    }

    public static List<String> head(Path file, int limit) throws IOException {
        Objects.requireNonNull(file, "file");
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be >= 0");
        }
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.limit(limit).toList();
        }
    }
}
