package collections;

public record CourseCode(String value) implements Comparable<CourseCode> {

    public CourseCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("code required");
        }
        value = value.trim().toUpperCase();
    }

    public static CourseCode of(String raw) {
        return new CourseCode(raw);
    }

    @Override
    public int compareTo(CourseCode other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
