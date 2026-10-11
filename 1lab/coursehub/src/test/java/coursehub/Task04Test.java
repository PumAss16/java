package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Задание 4. Порядок try / catch / finally.
 * finally выполняется всегда и последним, но возвращать из него нельзя:
 * return обрывает метод, поэтому значение из try/catch теряется, а необработанное
 * исключение исчезает вовсе (suppressed при этом не создаётся — механизм подавления
 * есть только у try-with-resources).
 */
@DisplayName("Задание 4. try-catch-finally")
class Task04Test {

    private int returnFromTry(List<String> log) {
        try {
            log.add("try");
            return 1;
        } finally {
            log.add("finally");
        }
    }

    private int throwThenCatch(List<String> log) {
        try {
            log.add("try");
            throw new IllegalStateException("boom");
        } catch (IllegalStateException e) {
            log.add("catch");
            return 2;
        } finally {
            log.add("finally");
        }
    }

    @SuppressWarnings("finally") // намеренно показываем вред return из finally
    private int returnFromFinally(List<String> log) {
        try {
            log.add("try");
            return 1;
        } finally {
            log.add("finally");
            return 3;
        }
    }

    @SuppressWarnings("finally") // return скрывает исключение — это и есть предмет задания
    private int finallyHidesException(List<String> log) {
        try {
            log.add("try");
            throw new IllegalArgumentException("primary");
        } finally {
            log.add("finally");
            return 3;
        }
    }

    @SuppressWarnings("finally") // throw из finally заменяет основное исключение
    private void throwFromFinally(List<String> log) {
        try {
            log.add("try");
            throw new IllegalArgumentException("primary");
        } finally {
            log.add("finally");
            throw new IllegalStateException("from finally");
        }
    }

    @Test
    @DisplayName("return в try: try -> finally, значение из try")
    void returnInTry() {
        List<String> log = new ArrayList<>();

        assertEquals(1, returnFromTry(log));
        assertEquals(List.of("try", "finally"), log);
    }

    @Test
    @DisplayName("throw: try -> catch -> finally")
    void throwInTry() {
        List<String> log = new ArrayList<>();

        assertEquals(2, throwThenCatch(log));
        assertEquals(List.of("try", "catch", "finally"), log);
    }

    @Test
    @DisplayName("return в finally перекрывает значение из try")
    void returnInFinallyWins() {
        List<String> log = new ArrayList<>();

        assertEquals(3, returnFromFinally(log));
        assertEquals(List.of("try", "finally"), log);
    }

    @Test
    @DisplayName("return в finally скрывает необработанное исключение")
    void returnInFinallyHidesException() {
        List<String> log = new ArrayList<>();

        int result = assertDoesNotThrow(() -> finallyHidesException(log));

        assertAll(
                () -> assertEquals(3, result),
                () -> assertEquals(List.of("try", "finally"), log));
    }

    @Test
    @DisplayName("throw из finally заменяет основное исключение, suppressed не появляется")
    void throwFromFinallyReplaces() {
        List<String> log = new ArrayList<>();

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> throwFromFinally(log));

        assertAll(
                () -> assertEquals("from finally", e.getMessage()),
                () -> assertEquals(0, e.getSuppressed().length),
                () -> assertEquals(List.of("try", "finally"), log));
    }
}
