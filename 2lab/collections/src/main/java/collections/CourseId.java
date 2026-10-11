package collections;

public record CourseId(String value) implements Comparable<CourseId> {

    public CourseId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("id required");
        }
        value = value.trim();
    }

    public static CourseId of(String raw) {
        return new CourseId(raw);
    }

    @Override
    public int compareTo(CourseId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
