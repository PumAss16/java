package coursehub.practice8;

public record Anomaly(long index, String studentId, String courseCode, String reason) {

    public Anomaly {
        if (index < 1) {
            throw new IllegalArgumentException("index must be >= 1");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
