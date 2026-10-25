package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Task02Test {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("UTF-8: строки и символы считаются, байты отличаются от chars")
    void copiesUtf8AndCounts() throws IOException {
        Path source = tempDir.resolve("source.txt");
        Path target = tempDir.resolve("target.txt");
        Files.writeString(source, "Привет\nмир\n", StandardCharsets.UTF_8);

        Task2BufferedText.TextStats stats =
                Task2BufferedText.copy(source, target, StandardCharsets.UTF_8);

        assertEquals(2, stats.lines());
        assertEquals(11, stats.characters());
        assertEquals(20, stats.bytes());
        assertEquals(Files.readString(source, StandardCharsets.UTF_8),
                Files.readString(target, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("UTF-16: BOM и двухбайтовые символы увеличивают размер файла")
    void sameCharactersDifferentBytesInUtf16() throws IOException {
        Path source = tempDir.resolve("source-utf16.txt");
        Path target = tempDir.resolve("target-utf16.txt");
        Files.writeString(source, "Привет\n", StandardCharsets.UTF_16);

        Task2BufferedText.TextStats stats =
                Task2BufferedText.copy(source, target, StandardCharsets.UTF_16);

        assertEquals(1, stats.lines());
        assertEquals(7, stats.characters());
        assertEquals(16, stats.bytes());
        assertTrue(stats.bytes() > stats.characters());
        assertEquals("Привет\n", Files.readString(target, StandardCharsets.UTF_16));
    }

    @Test
    @DisplayName("пустой файл даёт ноль строк и символов")
    void emptyFileHasZeroCounts() throws IOException {
        Path source = tempDir.resolve("empty.txt");
        Path target = tempDir.resolve("empty-copy.txt");
        Files.writeString(source, "");

        Task2BufferedText.TextStats stats =
                Task2BufferedText.copy(source, target, StandardCharsets.UTF_8);

        assertEquals(0, stats.lines());
        assertEquals(0, stats.characters());
        assertEquals(0, stats.bytes());
    }

    @Test
    @DisplayName("последняя строка без перевода строки всё равно считается")
    void lastLineWithoutTerminatorCounts() throws IOException {
        Path source = tempDir.resolve("one-two.txt");
        Path target = tempDir.resolve("one-two-copy.txt");
        Files.writeString(source, "one\ntwo", StandardCharsets.UTF_8);

        Task2BufferedText.TextStats stats =
                Task2BufferedText.copy(source, target, StandardCharsets.UTF_8);

        assertEquals(2, stats.lines());
        assertEquals(7, stats.characters());
        assertEquals(7, stats.bytes());
    }

    @Test
    @DisplayName("CRLF и одиночный CR считаются одним переводом строки")
    void crlfAndCrAreSingleLineBreaks() throws IOException {
        Path source = tempDir.resolve("line-endings.txt");
        Path target = tempDir.resolve("line-endings-copy.txt");
        Files.writeString(source, "a\r\nb\rc\n", StandardCharsets.UTF_8);

        Task2BufferedText.TextStats stats =
                Task2BufferedText.copy(source, target, StandardCharsets.UTF_8);

        assertEquals(3, stats.lines());
        assertEquals(7, stats.characters());
        assertEquals(7, stats.bytes());
    }
}
