package coursehub.practice6.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task08Test {

    @Test
    @DisplayName("исходная дата не меняется")
    void startDateIsImmutable() {
        LocalDate start = LocalDate.of(2024, 2, 1);
        StudyCalendar.moduleStarts(start, 4, 2);
        StudyCalendar.lastStudyDay(start, 8);
        assertEquals(LocalDate.of(2024, 2, 1), start);
    }

    @Test
    @DisplayName("даты начала модулей")
    void moduleStartsEveryTwoWeeks() {
        List<LocalDate> starts = StudyCalendar.moduleStarts(LocalDate.of(2024, 2, 1), 4, 2);
        assertEquals(List.of(
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 2, 15),
                LocalDate.of(2024, 2, 29),
                LocalDate.of(2024, 3, 14)), starts);
    }

    @Test
    @DisplayName("последний учебный день")
    void lastStudyDayAfterEightWeeks() {
        assertEquals(LocalDate.of(2024, 3, 27), StudyCalendar.lastStudyDay(LocalDate.of(2024, 2, 1), 8));
    }

    @Test
    @DisplayName("високосные годы")
    void leapYears() {
        assertTrue(StudyCalendar.isLeapYear(2024));
        assertFalse(StudyCalendar.isLeapYear(2100));
        assertTrue(StudyCalendar.isLeapYear(2000));
    }

    @Test
    @DisplayName("29 февраля в високосный год")
    void leapDayInLeapYear() {
        assertEquals(LocalDate.of(2024, 2, 29), StudyCalendar.leapDay(2024));
    }

    @Test
    @DisplayName("29 февраля в обычный год запрещено")
    void leapDayInCommonYearFails() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> StudyCalendar.leapDay(2023));
        assertTrue(error.getMessage().contains("2023"));
    }
}
