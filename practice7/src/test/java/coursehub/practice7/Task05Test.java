package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Task05Test {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("бинарный каталог читается обратно без потерь")
    void roundTrip() throws IOException {
        Path file = tempDir.resolve("catalog.bin");
        List<Course> courses = List.of(
                new Course("C-1", "Java: коллекции"),
                new Course("C-2", "SQL"));

        Task5BinaryCatalog.write(file, courses);

        assertEquals(courses, Task5BinaryCatalog.read(file));
    }

    @Test
    @DisplayName("пустой каталог — это count=0")
    void emptyCatalog() throws IOException {
        Path file = tempDir.resolve("empty.bin");

        Task5BinaryCatalog.write(file, List.of());

        assertEquals(List.of(), Task5BinaryCatalog.read(file));
    }

    @Test
    @DisplayName("неизвестная версия отклоняется")
    void unknownVersionIsRejected() throws IOException {
        Path file = tempDir.resolve("future.bin");
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
            out.writeInt(Task5BinaryCatalog.MAGIC);
            out.writeInt(99);
            out.writeInt(0);
        }

        UnsupportedVersionException error = assertThrows(UnsupportedVersionException.class,
                () -> Task5BinaryCatalog.read(file));

        assertEquals(99, error.version());
    }

    @Test
    @DisplayName("обрезанный файл отклоняется")
    void truncatedFileIsRejected() throws IOException {
        Path file = tempDir.resolve("truncated.bin");
        Task5BinaryCatalog.write(file, List.of(new Course("C-1", "Java")));
        byte[] bytes = Files.readAllBytes(file);
        Files.write(file, Arrays.copyOf(bytes, bytes.length - 2));

        assertThrows(TruncatedCatalogException.class, () -> Task5BinaryCatalog.read(file));
    }

    @Test
    @DisplayName("неверный magic отклоняется")
    void badMagicIsRejected() throws IOException {
        Path file = tempDir.resolve("bad-magic.bin");
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
            out.writeInt(0x12345678);
            out.writeInt(Task5BinaryCatalog.VERSION);
            out.writeInt(0);
        }

        CatalogFormatException error = assertThrows(CatalogFormatException.class,
                () -> Task5BinaryCatalog.read(file));

        assertTrue(error.getMessage().contains("magic"));
    }
}
