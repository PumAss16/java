package coursehub.practice7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public final class Task2BufferedText {

    private static final int BUFFER_SIZE = 8 * 1024;

    private Task2BufferedText() {
    }

    public static TextStats copy(Path source, Path target, Charset charset) throws IOException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(charset, "charset");

        long characters = 0;
        long lines = 0;
        boolean hasCharacters = false;
        boolean pendingCr = false;
        boolean lastWasTerminator = false;

        try (BufferedReader reader = Files.newBufferedReader(source, charset);
             BufferedWriter writer = Files.newBufferedWriter(target, charset,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE)) {

            char[] buffer = new char[BUFFER_SIZE];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
                characters += read;
                for (int i = 0; i < read; i++) {
                    hasCharacters = true;
                    char current = buffer[i];

                    if (pendingCr) {
                        lines++;
                        pendingCr = false;
                        if (current == '\n') {
                            lastWasTerminator = true;
                            continue;
                        }
                    }

                    if (current == '\r') {
                        pendingCr = true;
                        lastWasTerminator = true;
                    } else if (current == '\n') {
                        lines++;
                        lastWasTerminator = true;
                    } else {
                        lastWasTerminator = false;
                    }
                }
            }
            writer.flush();
        }

        if (pendingCr) {
            lines++;
            lastWasTerminator = true;
        }
        if (hasCharacters && !lastWasTerminator) {
            lines++;
        }
        return new TextStats(lines, characters, Files.size(target));
    }

    public record TextStats(long lines, long characters, long bytes) {
    }
}
