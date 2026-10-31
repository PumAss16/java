package coursehub.practice8;

public record CourseCompletion(
        String courseCode,
        int enrolled,
        int completed,
        double completionRate) {

    public CourseCompletion {
        if (courseCode == null || courseCode.isBlank()) {
            throw new IllegalArgumentException("courseCode must not be blank");
        }
        if (enrolled < 0 || completed < 0) {
            throw new IllegalArgumentException("counts must be >= 0");
        }
        if (completed > enrolled) {
            throw new IllegalArgumentException("completed must not exceed enrolled");
        }
        if (completionRate < 0 || completionRate > 1) {
            throw new IllegalArgumentException("completionRate must be between 0 and 1");
        }
    }
}
