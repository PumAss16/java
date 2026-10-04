package coursehub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Задание 2. Десять ситуаций и выбор класса отказа.
 *
 * checked   — наследник Exception, но не RuntimeException: компилятор требует решения
 *             (файл, сеть, импорт, прерывание);
 * unchecked — RuntimeException: неверный аргумент, нарушенное состояние, баг, ошибка конфигурации;
 * error     — Error (нет памяти): не восстанавливаем и не ловим;
 * value     — отказ выражается значением (Optional), а не исключением.
 */
@DisplayName("Задание 2. Checked / unchecked / значение / Error")
class Task02Test {

    static Stream<Arguments> situations() {
        return Stream.of(
                Arguments.of("1 неверный аргумент метода", ValidationException.class, "unchecked"),
                Arguments.of("2 отсутствующий файл", NoSuchFileException.class, "checked"),
                Arguments.of("3 нарушение состояния", IllegalStateException.class, "unchecked"),
                Arguments.of("4 сетевой отказ", ConnectException.class, "checked"),
                Arguments.of("5 ошибка конфигурации", NumberFormatException.class, "unchecked"),
                Arguments.of("6 недостаток памяти", OutOfMemoryError.class, "error"),
                Arguments.of("7 некорректная запись импорта", CourseImportException.class, "checked"),
                Arguments.of("8 ничего не найдено при поиске", null, "value"),
                Arguments.of("9 прерывание потока", InterruptedException.class, "checked"),
                Arguments.of("10 непредвиденный баг", NullPointerException.class, "unchecked"));
    }

    @ParameterizedTest(name = "{0} -> {2}")
    @MethodSource("situations")
    @DisplayName("Класс отказа следует из типа")
    void classifies(String name, Class<? extends Throwable> type, String kind) {
        switch (kind) {
            case "checked" -> {
                assertTrue(Exception.class.isAssignableFrom(type), name);
                assertFalse(RuntimeException.class.isAssignableFrom(type), name);
            }
            case "unchecked" -> assertTrue(RuntimeException.class.isAssignableFrom(type), name);
            case "error" -> {
                assertTrue(Error.class.isAssignableFrom(type), name);
                assertFalse(Exception.class.isAssignableFrom(type), name);
            }
            case "value" -> assertNull(type, name);
            default -> fail("неизвестный класс: " + kind);
        }
    }

    @Test
    @DisplayName("2. Отсутствующий файл — checked")
    void missingFileIsChecked() {
        assertThrows(NoSuchFileException.class, () -> Files.readString(Path.of("no-such-file-12345.txt")));
    }

    @Test
    @DisplayName("4. Сетевой отказ восстановим повтором")
    void networkFailureIsRetryable() throws ConnectException {
        class Flaky {
            private int attempts;

            String fetch() throws ConnectException {
                if (++attempts == 1) {
                    throw new ConnectException("connection refused");
                }
                return "catalog";
            }
        }
        Flaky flaky = new Flaky();

        assertThrows(ConnectException.class, flaky::fetch);
        assertEquals("catalog", flaky.fetch());
    }

    @Test
    @DisplayName("6. Недостаток памяти — Error, не Exception")
    void outOfMemoryIsAnError() {
        Throwable failure = new OutOfMemoryError("heap");

        assertTrue(failure instanceof Error);
        assertFalse(failure instanceof Exception);
    }

    @Test
    @DisplayName("8. «Не найдено» — значение Optional.empty")
    void notFoundIsAValue() {
        assertEquals(Optional.empty(), new CourseCatalog().findCourse(CourseCode.of("CS-101")));
    }

    @Test
    @DisplayName("9. Прерывание — checked, флаг прерывания восстанавливается")
    void interruptionRestoresFlag() {
        Thread.currentThread().interrupt();
        try {
            try {
                Thread.sleep(20);
                fail("sleep должен быть прерван");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    @DisplayName("1. Неверный аргумент — unchecked, ловить не нужно")
    void invalidArgumentIsUnchecked() {
        assertThrows(ValidationException.class, () -> CourseCode.of("!!!"));
    }
}
