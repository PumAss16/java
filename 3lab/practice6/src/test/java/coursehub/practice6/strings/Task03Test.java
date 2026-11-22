package coursehub.practice6.strings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task03Test {

    @Test
    @DisplayName("Валидные коды разбираются на префикс и номер")
    void validCodesAreParsed() {
        check("JAVA-101", "JAVA", 101);
        check("PY-001", "PY", 1);
        check("DB-999", "DB", 999);
        check("WEB-010", "WEB", 10);
        check("MATH-500", "MATH", 500);
        check("CS-100", "CS", 100);
    }

    @Test
    @DisplayName("Невалидные коды бросают IllegalArgumentException")
    void invalidCodesThrowIllegalArgumentException() {
        String[] invalid = {
            "JAVA-10",
            "JAVA-1011",
            "JAVA-101 ",
            " JAVA-101",
            "JAVA101",
            "java-101",
            "JAVA-1O1",
            "JAVA-",
            "JAVA-101extra",
            "prefixJAVA-101",
            "ＪＡＶＡ-101",
            "JAVA-１０１",
            "JAVA-101\n"
        };
        for (String raw : invalid) {
            assertInvalid(raw);
        }
    }

    @Test
    @DisplayName("null не парсится и даёт то же сообщение об ошибке")
    void nullIsRejected() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> CourseCode.parse(null));
        assertEquals("invalid course code: null", error.getMessage());
    }

    @Test
    @DisplayName("Хвост после кода не допускается: matches, а не find")
    void trailingTextIsRejected() {
        assertInvalid("JAVA-101extra");
    }

    @Test
    @DisplayName("Префикс перед кодом не допускается: matches, а не find")
    void leadingTextIsRejected() {
        assertInvalid("prefixJAVA-101");
    }

    @Test
    @DisplayName("Равенство и toString строятся по value")
    void equalityAndToStringUseValue() {
        CourseCode code = CourseCode.parse("JAVA-101");
        assertEquals("JAVA-101", code.toString());
        assertEquals(CourseCode.parse("JAVA-101"), code);
        assertEquals(CourseCode.parse("JAVA-101").hashCode(), code.hashCode());
        assertNotEquals(CourseCode.parse("JAVA-102"), code);
    }

    private static void check(String raw, String prefix, int number) {
        CourseCode code = CourseCode.parse(raw);
        assertEquals(raw, code.value());
        assertEquals(prefix, code.prefix());
        assertEquals(number, code.number());
        assertEquals(raw, code.toString());
    }

    private static void assertInvalid(String raw) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> CourseCode.parse(raw));
        assertEquals("invalid course code: " + raw, error.getMessage());
    }
}
