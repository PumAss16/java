package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Задание 5. Try-with-resources: ресурс логирует open/use/close и закрывается
 * при успехе и при исключении. Если падают и работа, и close, основным остаётся
 * исключение из работы, а ошибка close попадает в getSuppressed().
 */
@DisplayName("Задание 5. Try-with-resources и suppressed")
class Task05Test {

    /** Тестовый AutoCloseable: конструктор = open, use() и close() умеют падать. */
    static class Resource implements AutoCloseable {

        private final String name;
        private final List<String> log;
        private final boolean failUse;
        private final boolean failClose;

        Resource(String name, List<String> log, boolean failUse, boolean failClose) {
            this.name = name;
            this.log = log;
            this.failUse = failUse;
            this.failClose = failClose;
            log.add("open:" + name);
        }

        String use() {
            log.add("use:" + name);
            if (failUse) {
                throw new IllegalStateException("use failed: " + name);
            }
            return name;
        }

        @Override
        public void close() throws IOException {
            log.add("close:" + name);
            if (failClose) {
                throw new IOException("close failed: " + name);
            }
        }
    }

    @Test
    @DisplayName("Успех: open -> use -> close")
    void success() throws IOException {
        List<String> log = new ArrayList<>();
        String value;
        try (Resource r = new Resource("A", log, false, false)) {
            value = r.use();
        }

        assertAll(
                () -> assertEquals("A", value),
                () -> assertEquals(List.of("open:A", "use:A", "close:A"), log));
    }

    @Test
    @DisplayName("Исключение в работе: ресурс всё равно закрыт")
    void closesAfterFailureInUse() {
        List<String> log = new ArrayList<>();

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            try (Resource r = new Resource("B", log, true, false)) {
                r.use();
            }
        });

        assertAll(
                () -> assertEquals("use failed: B", e.getMessage()),
                () -> assertEquals(List.of("open:B", "use:B", "close:B"), log),
                () -> assertEquals(0, e.getSuppressed().length));
    }

    @Test
    @DisplayName("Падают use и close: ошибка закрытия подавлена")
    void bothFail() {
        List<String> log = new ArrayList<>();

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            try (Resource r = new Resource("C", log, true, true)) {
                r.use();
            }
        });

        assertAll(
                () -> assertEquals("use failed: C", e.getMessage()),
                () -> assertEquals(1, e.getSuppressed().length),
                () -> assertInstanceOf(IOException.class, e.getSuppressed()[0]),
                () -> assertEquals("close failed: C", e.getSuppressed()[0].getMessage()),
                () -> assertEquals(List.of("open:C", "use:C", "close:C"), log));
    }

    @Test
    @DisplayName("Падает только close: ошибка закрытия становится основной")
    void onlyCloseFails() {
        List<String> log = new ArrayList<>();

        IOException e = assertThrows(IOException.class, () -> {
            try (Resource r = new Resource("D", log, false, true)) {
                r.use();
            }
        });

        assertAll(
                () -> assertEquals("close failed: D", e.getMessage()),
                () -> assertEquals(0, e.getSuppressed().length));
    }

    @Test
    @DisplayName("Два ресурса: подавленные в порядке закрытия (последний объявленный — первым)")
    void twoResources() {
        List<String> log = new ArrayList<>();

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            try (Resource outer = new Resource("OUT", log, true, true);
                 Resource inner = new Resource("IN", log, false, true)) {
                outer.use();
            }
        });

        assertAll(
                () -> assertEquals(2, e.getSuppressed().length),
                () -> assertEquals("close failed: IN", e.getSuppressed()[0].getMessage()),
                () -> assertEquals("close failed: OUT", e.getSuppressed()[1].getMessage()),
                () -> assertEquals(List.of("open:OUT", "open:IN", "use:OUT", "close:IN", "close:OUT"), log));
    }
}
