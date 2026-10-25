package coursehub.practice7;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public final class Task6AtomicReplace {

    private Task6AtomicReplace() {
    }

    public enum MoveKind {
        ATOMIC,
        FALLBACK
    }

    public record ReplaceResult(Path target, MoveKind moveKind) {
    }

    @FunctionalInterface
    interface MoveOperation {
        void move(Path source, Path target, boolean atomic) throws IOException;
    }

    public static ReplaceResult replace(Path target, String content) throws IOException {
        return replace(target, content, Task6AtomicReplace::moveFile);
    }

    static ReplaceResult replace(Path target, String content, MoveOperation moveOperation)
            throws IOException {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(moveOperation, "moveOperation");

        Path absoluteTarget = target.toAbsolutePath();
        Path directory = absoluteTarget.getParent();
        if (directory == null) {
            throw new IOException("target has no parent directory: " + target);
        }
        Files.createDirectories(directory);

        Path temp = Files.createTempFile(directory, ".catalog-", ".tmp");
        try {
            try (OutputStream out = Files.newOutputStream(temp,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                out.write(content.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }

            try {
                moveOperation.move(temp, absoluteTarget, true);
                return new ReplaceResult(absoluteTarget, MoveKind.ATOMIC);
            } catch (AtomicMoveNotSupportedException e) {
                moveOperation.move(temp, absoluteTarget, false);
                return new ReplaceResult(absoluteTarget, MoveKind.FALLBACK);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    private static void moveFile(Path source, Path target, boolean atomic) throws IOException {
        if (atomic) {
            Files.move(source, target,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
