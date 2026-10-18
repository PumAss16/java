package coursehub.practice6.time;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

class StudyCalendar {

    static List<LocalDate> moduleStarts(LocalDate start, int modules, int weeksBetween) {
        List<LocalDate> result = new ArrayList<>();
        for (int i = 0; i < modules; i++) {
            result.add(start.plusWeeks((long) i * weeksBetween));
        }
        return result;
    }

    static LocalDate lastStudyDay(LocalDate start, int weeks) {
        return start.plusWeeks(weeks).minusDays(1);
    }

    static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    static LocalDate leapDay(int year) {
        if (!Year.isLeap(year)) {
            throw new IllegalArgumentException("not a leap year: " + year);
        }
        return LocalDate.of(year, 2, 29);
    }
}
