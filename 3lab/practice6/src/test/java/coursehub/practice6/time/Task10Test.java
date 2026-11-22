package coursehub.practice6.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task10Test {

    private static InvalidDateFieldException failure(String text) {
        return assertThrows(InvalidDateFieldException.class, () -> DateParser.parse(text));
    }

    @Test
    @DisplayName("корректная дата разбирается")
    void validDate() {
        assertEquals(LocalDate.of(2024, 2, 29), DateParser.parse("29.02.2024"));
    }

    @Test
    @DisplayName("29 февраля невисокосного года")
    void invalidLeapDay() {
        InvalidDateFieldException error = failure("29.02.2023");
        assertEquals("dayOfMonth", error.field());
        assertEquals("29.02.2023", error.value());
        assertNotNull(error.getCause());
    }

    @Test
    @DisplayName("тридцатое апреля запрещено")
    void invalidDayInMonth() {
        assertEquals("dayOfMonth", failure("31.04.2024").field());
    }

    @Test
    @DisplayName("месяц вне диапазона")
    void invalidMonth() {
        assertEquals("month", failure("13.13.2024").field());
    }

    @Test
    @DisplayName("лишний текст в строке")
    void extraText() {
        InvalidDateFieldException error = failure("01.01.2024 extra");
        assertEquals("date", error.field());
        assertEquals("01.01.2024 extra", error.value());
    }

    @Test
    @DisplayName("одна цифра в дне и месяце")
    void shortFormat() {
        assertEquals("date", failure("1.1.2024").field());
    }

    @Test
    @DisplayName("сообщение содержит поле и значение")
    void messageHasFieldAndValue() {
        InvalidDateFieldException error = failure("32.01.2024");
        assertTrue(error.getMessage().contains("dayOfMonth"));
        assertTrue(error.getMessage().contains("32.01.2024"));
    }
}
