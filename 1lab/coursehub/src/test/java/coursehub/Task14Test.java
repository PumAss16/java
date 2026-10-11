package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Задание 14. Импортёр из абстрактного RecordReader с двумя отказами.
 * Malformed record -> checked CourseImportException с номером записи и cause.
 * Ошибка закрытия -> IOException в getSuppressed(), если основное уже есть,
 * либо InfrastructureException, если тело отработало успешно.
 * Частично опубликованного результата нет: список возвращается только при полном успехе.
 */
@DisplayName("Задание 14. Импорт с двумя отказами")
class Task14Test {

    /** Тестовый источник: строки, счётчики и флаги отказов. */
    static class FakeReader implements RecordReader {

        private final List<String> lines;
        private final boolean failOnClose;
        private int reads;
        private int closes;
        private boolean closed;

        FakeReader(boolean failOnClose, String... lines) {
            this.failOnClose = failOnClose;
            this.lines = Arrays.asList(lines);
        }

        @Override
        public String readRecord() {
            return reads < lines.size() ? lines.get(reads++) : null;
        }

        @Override
        public void close() throws IOException {
            closes++;
            closed = true;
            if (failOnClose) {
                throw new IOException("close failed");
            }
        }
    }

    private static final String GOOD = "CS-101;Java;30;40";
    private static final String BAD = "CS-102;Broken;30;oops";

    private final CourseImporter importer = new CourseImporter();

    @Test
    @DisplayName("Успех: записи разобраны, читатель закрыт")
    void success() throws CourseImportException {
        FakeReader reader = new FakeReader(false, GOOD, "MATH-101;Math;25;60");

        List<Course> courses = importer.importAll(reader);

        assertAll(
                () -> assertEquals(2, courses.size()),
                () -> assertEquals("CS-101", courses.get(0).code().value()),
                () -> assertEquals("MATH-101", courses.get(1).code().value()),
                () -> assertTrue(reader.closed),
                () -> assertEquals(1, reader.closes));
    }

    @Test
    @DisplayName("Malformed record №2: номер записи, cause, счётчик и отсутствие результата")
    void malformedRecord() {
        FakeReader reader = new FakeReader(false, GOOD, BAD, "WEB-404;Web;20;30");

        CourseImportException e = assertThrows(CourseImportException.class, () -> importer.importAll(reader));

        assertAll(
                () -> assertEquals(2, e.recordNumber()),
                () -> assertEquals(1, e.processed(), "обработана одна запись"),
                () -> assertEquals(2, reader.reads, "прочитаны две записи, вторая битая"),
                () -> assertInstanceOf(NumberFormatException.class, e.getCause()),
                () -> assertTrue(reader.closed, "ресурс закрыт несмотря на отказ"),
                () -> assertEquals(0, e.getSuppressed().length, "close прошёл успешно"));
    }

    @Test
    @DisplayName("Malformed + отказ close: основное — ошибка данных, suppressed — IOException")
    void malformedRecordAndCloseFailure() {
        FakeReader reader = new FakeReader(true, GOOD, BAD);

        CourseImportException e = assertThrows(CourseImportException.class, () -> importer.importAll(reader));

        assertAll(
                () -> assertEquals(2, e.recordNumber()),
                () -> assertInstanceOf(NumberFormatException.class, e.getCause()),
                () -> assertEquals(1, e.getSuppressed().length),
                () -> assertInstanceOf(IOException.class, e.getSuppressed()[0]),
                () -> assertEquals("close failed", e.getSuppressed()[0].getMessage()),
                () -> assertTrue(reader.closed));
    }

    @Test
    @DisplayName("Успешный разбор + отказ close: InfrastructureException, результата нет")
    void closeFailureAfterSuccessfulParse() {
        FakeReader reader = new FakeReader(true, GOOD);

        InfrastructureException e = assertThrows(InfrastructureException.class, () -> importer.importAll(reader));

        assertAll(
                () -> assertEquals("CH-4001", e.code()),
                () -> assertInstanceOf(IOException.class, e.getCause()),
                () -> assertEquals("close failed", e.getCause().getMessage()),
                () -> assertTrue(reader.closed));
    }

    @Test
    @DisplayName("Сбой чтения в середине потока: инфраструктурный отказ с числом записей")
    void readFailureKeepsCount() {
        RecordReader broken = new RecordReader() {
            private int reads;

            @Override
            public String readRecord() throws IOException {
                if (++reads > 2) {
                    throw new IOException("disk error");
                }
                return GOOD;
            }

            @Override
            public void close() {
            }
        };

        InfrastructureException e = assertThrows(InfrastructureException.class, () -> importer.importAll(broken));

        assertAll(
                () -> assertEquals("CH-4001", e.code()),
                () -> assertTrue(e.getMessage().contains("2"), e.getMessage()));
    }
}
