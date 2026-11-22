package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Task08Test {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("счётчики считают все строки, список ошибок ограничен")
    void countsAndLimitsStoredErrors() throws IOException {
        Path file = writeLines(100, 20);

        Task8StreamingImport.Stats stats = Task8StreamingImport.scan(
                file, new Task8StreamingImport.Options(2, 1000));

        assertEquals(100, stats.linesRead());
        assertEquals(95, stats.validLines());
        assertEquals(5, stats.invalidLines());
        assertEquals(5, stats.totalErrors());
        assertFalse(stats.cancelled());
        assertEquals(List.of(20L, 40L),
                stats.errors().stream().map(ImportError::lineNumber).toList());
    }

    @Test
    @DisplayName("после порога ошибок чтение прекращается")
    void cancelsAfterErrorThreshold() throws IOException {
        Path file = writeLines(100, 10);

        Task8StreamingImport.Stats stats = Task8StreamingImport.scan(
                file, new Task8StreamingImport.Options(10, 3));

        assertEquals(30, stats.linesRead());
        assertEquals(27, stats.validLines());
        assertEquals(3, stats.invalidLines());
        assertEquals(3, stats.totalErrors());
        assertTrue(stats.cancelled());
        assertEquals(3, stats.errors().size());
        assertEquals(30L, stats.errors().get(2).lineNumber());
    }

    @Test
    @DisplayName("большой файл читается построчно, без readAllLines")
    void largeFileIsProcessedStreaming() throws IOException {
        Path file = writeLines(200_000, 0);

        Task8StreamingImport.Stats stats = Task8StreamingImport.scan(
                file, new Task8StreamingImport.Options(10, 1_000_000));

        assertEquals(200_000, stats.linesRead());
        assertEquals(200_000, stats.validLines());
        assertEquals(0, stats.invalidLines());
        assertFalse(stats.cancelled());
    }

    @Test
    @DisplayName("пустой файл даёт нулевые счётчики")
    void emptyFileHasZeroCounts() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        Task8StreamingImport.Stats stats = Task8StreamingImport.scan(
                file, new Task8StreamingImport.Options(5, 10));

        assertEquals(0, stats.linesRead());
        assertEquals(0, stats.validLines());
        assertEquals(0, stats.totalErrors());
        assertFalse(stats.cancelled());
        assertTrue(stats.errors().isEmpty());
    }

    private Path writeLines(int count, int everyNthBad) throws IOException {
        Path file = tempDir.resolve("import.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int line = 1; line <= count; line++) {
                if (everyNthBad > 0 && line % everyNthBad == 0) {
                    writer.write("bad line " + line);
                } else {
                    writer.write("C-" + line + "|Course " + line);
                }
                writer.newLine();
            }
        }
        return file;
    }
}
