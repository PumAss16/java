package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Task04Test {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Files.lines внутри try-with-resources считает и закрывает поток")
    void totalCharactersUsesTryWithResources() throws IOException {
        Path file = writeFile("alpha\nbeta\ngamma\n");

        assertEquals(14, Task4LazyLines.totalCharacters(file));
    }

    @Test
    @DisplayName("head возвращает только запрошенные строки")
    void headLimitsResult() throws IOException {
        Path file = writeFile("alpha\nbeta\ngamma\n");

        assertEquals(List.of("alpha", "beta"), Task4LazyLines.head(file, 2));
        assertEquals(List.of(), Task4LazyLines.head(file, 0));
        assertEquals(List.of("alpha", "beta", "gamma"), Task4LazyLines.head(file, 10));
    }

    @Test
    @DisplayName("ошибочный возврат stream: закрывать обязан вызывающий")
    void brokenStreamMustBeClosedByCaller() throws IOException {
        Path file = writeFile("alpha\nbeta\ngamma\n");
        Stream<String> stream = brokenLines(file);

        try (stream) {
            assertEquals(List.of("alpha", "beta", "gamma"), stream.toList());
        }

        assertThrows(IllegalStateException.class, stream::count);
    }

    private static Stream<String> brokenLines(Path file) throws IOException {
        return Files.lines(file, StandardCharsets.UTF_8);
    }

    private Path writeFile(String content) throws IOException {
        Path file = tempDir.resolve("lines.txt");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
