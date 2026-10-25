package coursehub.practice7;

public record Course(String code, String title, int level) {

    public Course {
        requireText(code, "code");
        requireText(title, "title");
        if (code.indexOf('|') >= 0) {
            throw new IllegalArgumentException("code must not contain '|'");
        }
        if (code.indexOf('\n') >= 0 || code.indexOf('\r') >= 0
                || title.indexOf('\n') >= 0 || title.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("line breaks are not allowed in course fields");
        }
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
    }

    public Course(String code, String title) {
        this(code, title, 1);
    }

    public String textLine() {
        return code + "|" + title;
    }

    private static void requireText(String value, String name) {
        if (value == null) {
            throw new NullPointerException(name + " is required");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
