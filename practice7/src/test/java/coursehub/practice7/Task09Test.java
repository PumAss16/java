package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Task09Test {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("версия 2 читается вместе с уровнем курса")
    void version2RoundTrip() throws IOException {
        Path file = tempDir.resolve("catalog-v2.bin");
        List<Course> courses = List.of(
                new Course("C-1", "Java: коллекции", 3),
                new Course("C-2", "SQL", 1));

        Task9VersionedCatalog.write(file, courses);

        Task9VersionedCatalog.LoadedCatalog loaded = Task9VersionedCatalog.read(file);
        assertEquals(Task9VersionedCatalog.CURRENT_VERSION, loaded.schemaVersion());
        assertEquals(courses, loaded.courses());
    }

    @Test
    @DisplayName("версия 1 читается и мигрирует в версию 2")
    void version1IsMigratedToVersion2() throws IOException {
        Path source = tempDir.resolve("catalog-v1.bin");
        Path target = tempDir.resolve("catalog-v2.bin");
        writeV1(source, List.of(new Course("C-1", "Java"), new Course("C-2", "SQL")));

        Task9VersionedCatalog.LoadedCatalog v1 = Task9VersionedCatalog.read(source);
        assertEquals(1, v1.schemaVersion());
        assertEquals(List.of(new Course("C-1", "Java", 1), new Course("C-2", "SQL", 1)),
                v1.courses());

        Task9VersionedCatalog.LoadedCatalog migrated =
                Task9VersionedCatalog.migrateV1ToV2(source, target);

        assertEquals(2, migrated.schemaVersion());
        assertEquals(v1.courses(), migrated.courses());
        assertEquals(2, Task9VersionedCatalog.read(target).schemaVersion());
        assertEquals(1, Task9VersionedCatalog.read(source).schemaVersion());
    }

    @Test
    @DisplayName("будущая версия не читается как старая")
    void futureVersionIsRejected() throws IOException {
        Path file = tempDir.resolve("catalog-v3.bin");
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
            out.writeInt(Task9VersionedCatalog.MAGIC);
            out.writeInt(3);
            out.writeInt(0);
        }

        UnsupportedVersionException error = assertThrows(UnsupportedVersionException.class,
                () -> Task9VersionedCatalog.read(file));

        assertEquals(3, error.version());
    }

    @Test
    @DisplayName("повреждение payload обнаруживается до публикации")
    void corruptionIsDetectedBeforePublish() throws IOException {
        Path file = tempDir.resolve("catalog-corrupt.bin");
        Task9VersionedCatalog.write(file,
                List.of(new Course("C-1", "Java", 2), new Course("C-2", "SQL", 1)));

        byte[] bytes = Files.readAllBytes(file);
        bytes[bytes.length - 10] ^= 0x01;
        Files.write(file, bytes);

        List<Course> published = new ArrayList<>();
        CatalogFormatException error = assertThrows(CatalogFormatException.class,
                () -> Task9VersionedCatalog.readAndPublish(file, published::addAll));

        assertTrue(error.getMessage().contains("checksum"));
        assertTrue(published.isEmpty());
    }

    private static void writeV1(Path file, List<Course> courses) throws IOException {
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
            out.writeInt(Task9VersionedCatalog.MAGIC);
            out.writeInt(Task9VersionedCatalog.VERSION_1);
            out.writeInt(courses.size());
            for (Course course : courses) {
                out.writeUTF(course.code());
                out.writeUTF(course.title());
            }
        }
    }
}
