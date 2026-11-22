package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Task06Test {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("существующий файл заменяется, temp не остаётся")
    void replacesExistingFile() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("catalog"));
        Path target = directory.resolve("courses.txt");
        Files.writeString(target, "old", StandardCharsets.UTF_8);

        Task6AtomicReplace.ReplaceResult result =
                Task6AtomicReplace.replace(target, "new");

        assertEquals("new", Files.readString(target, StandardCharsets.UTF_8));
        assertTrue(result.moveKind() == Task6AtomicReplace.MoveKind.ATOMIC
                || result.moveKind() == Task6AtomicReplace.MoveKind.FALLBACK);
        assertNoTempFiles(directory);
    }

    @Test
    @DisplayName("целевой файл создаётся вместе с каталогом")
    void createsMissingTarget() throws IOException {
        Path target = tempDir.resolve("nested/catalog/courses.txt");

        Task6AtomicReplace.replace(target, "created");

        assertEquals("created", Files.readString(target, StandardCharsets.UTF_8));
        assertNoTempFiles(target.getParent());
    }

    @Test
    @DisplayName("без atomic move используется обычный move")
    void fallsBackWhenAtomicMoveIsNotSupported() throws IOException {
        Path target = tempDir.resolve("courses.txt");
        Files.writeString(target, "old", StandardCharsets.UTF_8);

        Task6AtomicReplace.MoveOperation mover = (source, destination, atomic) -> {
            if (atomic) {
                throw new AtomicMoveNotSupportedException(
                        source.toString(), destination.toString(), "test filesystem");
            }
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        };

        Task6AtomicReplace.ReplaceResult result =
                Task6AtomicReplace.replace(target, "fallback", mover);

        assertEquals(Task6AtomicReplace.MoveKind.FALLBACK, result.moveKind());
        assertEquals("fallback", Files.readString(target, StandardCharsets.UTF_8));
        assertNoTempFiles(tempDir);
    }

    @Test
    @DisplayName("при сорванном move старый файл цел, temp удалён")
    void failedMoveKeepsTargetAndCleansTemp() throws IOException {
        Path target = Files.createDirectory(tempDir.resolve("target-dir"));
        Files.writeString(target.resolve("keep.txt"), "old", StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> Task6AtomicReplace.replace(target, "new"));

        assertTrue(Files.isDirectory(target));
        assertEquals("old", Files.readString(target.resolve("keep.txt"), StandardCharsets.UTF_8));
        assertNoTempFiles(tempDir);
    }

    private static void assertNoTempFiles(Path directory) throws IOException {
        List<Path> tempFiles;
        try (Stream<Path> entries = Files.list(directory)) {
            tempFiles = entries
                    .filter(path -> path.getFileName().toString().startsWith(".catalog-"))
                    .toList();
        }
        assertTrue(tempFiles.isEmpty(), () -> "temporary files left: " + tempFiles);
    }
}
