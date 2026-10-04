package coursehub;

/** Код курса вида CS-101: нормализация входа и проверка формата (задание 10). */
public record CourseCode(String value) {

    public static CourseCode of(String raw) {
        if (raw == null) {
            throw new ValidationException("course code is required");
        }
        String s = raw.trim().toUpperCase()
                .replaceAll("[_\\s]+", "-")
                .replaceAll("-{2,}", "-");
        if (s.matches("[A-Z]{2,4}\\d{3}")) {
            s = s.substring(0, s.length() - 3) + "-" + s.substring(s.length() - 3);
        }
        if (!s.matches("[A-Z]{2,4}-\\d{3}")) {
            throw new ValidationException("invalid course code: " + raw);
        }
        return new CourseCode(s);
    }

    @Override
    public String toString() {
        return value;
    }
}
