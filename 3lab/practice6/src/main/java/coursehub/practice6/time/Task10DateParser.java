package coursehub.practice6.time;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DateParser {

    static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern SHAPE = Pattern.compile("^([0-9]{2})\\.([0-9]{2})\\.([0-9]{4})$");

    static LocalDate parse(String text) {
        if (text == null || text.isBlank()) {
            throw new InvalidDateFieldException("date", text, "date is required", null);
        }
        Matcher matcher = SHAPE.matcher(text);
        if (!matcher.matches()) {
            throw new InvalidDateFieldException("date", text, "invalid date format: " + text, null);
        }
        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int year = Integer.parseInt(matcher.group(3));
        if (month < 1 || month > 12) {
            throw new InvalidDateFieldException("month", text, "invalid month: " + text, null);
        }
        if (year < 1 || year > 9999) {
            throw new InvalidDateFieldException("year", text, "invalid year: " + text, null);
        }
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new InvalidDateFieldException("dayOfMonth", text, "invalid dayOfMonth: " + text, e);
        }
    }
}
