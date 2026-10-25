package coursehub.practice7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task03Test {

    @Test
    @DisplayName("исключение в середине закрывает writer, затем reader")
    void exceptionClosesWriterThenReader() {
        List<String> closeLog = new ArrayList<>();
        StringWriter target = new StringWriter();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> Task3ResourcePipeline.transform(
                        new StringReader("one\nboom\nthree\n"),
                        target,
                        line -> {
                            if (line.equals("boom")) {
                                throw new IllegalStateException("mapper failed");
                            }
                            return line.toUpperCase(Locale.ROOT);
                        },
                        closeLog));

        assertEquals("mapper failed", error.getMessage());
        assertEquals(List.of("writer", "reader"), closeLog);
        assertEquals("ONE" + System.lineSeparator(), target.toString());
    }

    @Test
    @DisplayName("успешный pipeline тоже закрывает оба ресурса")
    void successfulPipelineClosesBoth() throws IOException {
        List<String> closeLog = new ArrayList<>();
        StringWriter target = new StringWriter();

        Task3ResourcePipeline.transform(
                new StringReader("a\nb\n"),
                target,
                line -> line.toUpperCase(Locale.ROOT),
                closeLog);

        assertEquals(List.of("writer", "reader"), closeLog);
        assertEquals("A" + System.lineSeparator() + "B" + System.lineSeparator(),
                target.toString());
    }
}
