package coursehub.practice7;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Task8StreamingImport {

    private Task8StreamingImport() {
    }

    public record Options(int maxStoredErrors, int cancelAfterErrors) {

        public Options {
            if (maxStoredErrors < 0) {
                throw new IllegalArgumentException("maxStoredErrors must be >= 0");
            }
            if (cancelAfterErrors < 1) {
                throw new IllegalArgumentException("cancelAfterErrors must be >= 1");
            }
        }
    }

    public record Stats(long linesRead, long validLines, long invalidLines,
                        long totalErrors, boolean cancelled, List<ImportError> errors) {

        public Stats {
            errors = List.copyOf(errors);
        }
    }

    public static Stats scan(Path file, Options options) throws IOException {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(options, "options");

        long linesRead = 0;
        long validLines = 0;
        long invalidLines = 0;
        long totalErrors = 0;
        boolean cancelled = false;
        List<ImportError> errors = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                linesRead++;
                CatalogLineParser.ParsedLine parsed = CatalogLineParser.parse(line);
                if (parsed.valid()) {
                    validLines++;
                    continue;
                }

                invalidLines++;
                totalErrors++;
                if (errors.size() < options.maxStoredErrors()) {
                    errors.add(new ImportError(linesRead, parsed.error()));
                }
                if (totalErrors >= options.cancelAfterErrors()) {
                    cancelled = true;
                    break;
                }
            }
        }

        return new Stats(linesRead, validLines, invalidLines, totalErrors, cancelled, errors);
    }
}
