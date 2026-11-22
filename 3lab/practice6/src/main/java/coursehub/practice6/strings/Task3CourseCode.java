package coursehub.practice6.strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CourseCode {

    static final Pattern PATTERN = Pattern.compile("^(JAVA|PY|DB|WEB|MATH|CS)-([0-9]{3})$");

    private final String value;
    private final String prefix;
    private final int number;

    private CourseCode(String value, String prefix, int number) {
        this.value = value;
        this.prefix = prefix;
        this.number = number;
    }

    static CourseCode parse(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("invalid course code: " + raw);
        }
        Matcher matcher = PATTERN.matcher(raw);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("invalid course code: " + raw);
        }
        return new CourseCode(raw, matcher.group(1), Integer.parseInt(matcher.group(2)));
    }

    String value() {
        return value;
    }

    String prefix() {
        return prefix;
    }

    int number() {
        return number;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CourseCode)) {
            return false;
        }
        return value.equals(((CourseCode) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
