package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Task07Test {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("корректный файл даёт Success с полным каталогом")
    void validFileBecomesSuccess() throws IOException {
        Path file = write("C-1|Java\nC-2|SQL\n");

        Task7CatalogImport.ImportResult result = Task7CatalogImport.importFile(file);

        Task7CatalogImport.Success success =
                assertInstanceOf(Task7CatalogImport.Success.class, result);
        assertEquals(List.of(new Course("C-1", "Java"), new Course("C-2", "SQL")),
                success.courses());
    }

    @Test
    @DisplayName("все ошибки собираются с номерами строк")
    void allErrorsAreCollectedWithLineNumbers() {
        Task7CatalogImport.ImportResult result = Task7CatalogImport.parse(
                "C-1|Java\n"
                        + "broken line\n"
                        + "|No code\n"
                        + "C-1|Duplicate\n"
                        + "C-3|\n");

        Task7CatalogImport.Failure failure =
                assertInstanceOf(Task7CatalogImport.Failure.class, result);
        assertEquals(List.of(2L, 3L, 4L, 5L),
                failure.errors().stream().map(ImportError::lineNumber).toList());
        assertEquals(4, failure.errors().size());
        assertTrue(failure.errors().get(2).message().contains("duplicate"));
    }

    @Test
    @DisplayName("при ошибке ничего не публикуется")
    void failurePublishesNothing() throws IOException {
        Path file = write("C-1|Java\nbroken\n");
        List<Course> target = new ArrayList<>();
        target.add(new Course("OLD", "Old catalog"));

        Task7CatalogImport.ImportResult result =
                Task7CatalogImport.importAndPublish(file, target::addAll);

        assertInstanceOf(Task7CatalogImport.Failure.class, result);
        assertEquals(List.of(new Course("OLD", "Old catalog")), target);
    }

    @Test
    @DisplayName("при успехе публикуется весь каталог")
    void successPublishesWholeCatalog() throws IOException {
        Path file = write("C-1|Java\nC-2|SQL\n");
        List<Course> target = new ArrayList<>();

        Task7CatalogImport.ImportResult result =
                Task7CatalogImport.importAndPublish(file, target::addAll);

        assertInstanceOf(Task7CatalogImport.Success.class, result);
        assertEquals(List.of(new Course("C-1", "Java"), new Course("C-2", "SQL")), target);
    }

    private Path write(String content) throws IOException {
        Path file = tempDir.resolve("import.txt");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
