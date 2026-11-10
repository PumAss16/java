package coursehub;

public record Task10CourseCode(String value) {

    public static Task10CourseCode of(String raw) {
        if (raw == null) {
            throw new Task02ValidationException("course code is required");
        }
        String s = raw.trim().toUpperCase()
                .replaceAll("[_\\s]+", "-")
                .replaceAll("-{2,}", "-");
        if (s.matches("[A-Z]{2,4}\\d{3}")) {
            s = s.substring(0, s.length() - 3) + "-" + s.substring(s.length() - 3);
        }
        if (!s.matches("[A-Z]{2,4}-\\d{3}")) {
            throw new Task02ValidationException("invalid course code: " + raw);
        }
        return new Task10CourseCode(s);
    }

    @Override
    public String toString() {
        return value;
    }
}
