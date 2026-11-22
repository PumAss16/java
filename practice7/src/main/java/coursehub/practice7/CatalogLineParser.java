package coursehub.practice7;

final class CatalogLineParser {

    private CatalogLineParser() {
    }

    static ParsedLine parse(String line) {
        if (line == null) {
            return ParsedLine.error("line is null");
        }
        if (line.isBlank()) {
            return ParsedLine.error("blank line");
        }
        int separator = line.indexOf('|');
        if (separator < 0) {
            return ParsedLine.error("missing '|' separator");
        }
        String code = line.substring(0, separator).trim();
        String title = line.substring(separator + 1).trim();
        if (code.isEmpty()) {
            return ParsedLine.error("empty course code");
        }
        if (title.isEmpty()) {
            return ParsedLine.error("empty course title");
        }
        try {
            return ParsedLine.ok(new Course(code, title));
        } catch (IllegalArgumentException e) {
            return ParsedLine.error(e.getMessage());
        }
    }

    record ParsedLine(Course course, String error) {

        static ParsedLine ok(Course course) {
            return new ParsedLine(course, null);
        }

        static ParsedLine error(String message) {
            return new ParsedLine(null, message);
        }

        boolean valid() {
            return course != null;
        }
    }
}
