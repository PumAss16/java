package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Task01Test {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("каталог сохраняется в UTF-8 и читается обратно")
    void savesAndLoadsUtf8() throws IOException {
        Path file = tempDir.resolve("courses.txt");
        List<Course> courses = List.of(
                new Course("C-1", "Java: коллекции"),
                new Course("C-2", "Java | SQL"));

        Task1TextCatalog.save(file, courses);

        String raw = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(raw.contains("коллекции"));
        Task1TextCatalog.LoadResult result = Task1TextCatalog.load(file);
        assertTrue(result.found());
        assertEquals(courses, result.courses());
    }

    @Test
    @DisplayName("отсутствующий файл возвращается как found=false")
    void missingFileIsReported() throws IOException {
        Path file = tempDir.resolve("missing.txt");

        Task1TextCatalog.LoadResult result = Task1TextCatalog.load(file);

        assertFalse(result.found());
        assertTrue(result.courses().isEmpty());
        assertFalse(Files.exists(file));
    }

    @Test
    @DisplayName("каталог вместо файла — ошибка, а не пустой успех")
    void directoryInsteadOfFileFails() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("courses.txt"));

        IOException error = assertThrows(IOException.class,
                () -> Task1TextCatalog.load(directory));

        assertTrue(error.getMessage().contains("regular file"));
    }

    @Test
    @DisplayName("битая строка сообщает номер и не даёт частичный каталог")
    void brokenLineFailsWithLineNumber() throws IOException {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file, "C-1|Java\nbroken\nC-2|SQL\n", StandardCharsets.UTF_8);

        CatalogFormatException error = assertThrows(CatalogFormatException.class,
                () -> Task1TextCatalog.load(file));

        assertTrue(error.getMessage().contains("line 2"));
        assertTrue(error.getMessage().contains("separator"));
    }
}
