package coursehub;

public class Task06CourseParser {

    public Task00Course parse(String line) {
        String[] parts = line.split(";");
        if (parts.length != 4) {
            throw new NumberFormatException("expected 4 fields, got " + parts.length);
        }
        return new Task00Course(Task10CourseCode.of(parts[0].trim()), parts[1].trim(),
                Integer.parseInt(parts[3].trim()), Integer.parseInt(parts[2].trim()),
                0, Task13CourseStatus.DRAFT, null);
    }
}
