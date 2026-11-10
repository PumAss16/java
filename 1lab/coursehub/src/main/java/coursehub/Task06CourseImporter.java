package coursehub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Task06CourseImporter {

    private final Task06CourseParser parser = new Task06CourseParser();

    public Task00Course parseCourse(String line, int recordNumber) throws Task06CourseImportException {
        try {
            return parser.parse(line);
        } catch (NumberFormatException | Task02ValidationException e) {
            throw new Task06CourseImportException(recordNumber, "malformed record #" + recordNumber, e);
        }
    }

    public List<Task00Course> importAll(Task14RecordReader reader) throws Task06CourseImportException {
        List<Task00Course> courses = new ArrayList<>();
        int number = 0;
        try (reader) {
            String line;
            while ((line = reader.readRecord()) != null) {
                courses.add(parseCourse(line, ++number));
            }
        } catch (IOException e) {
            throw new Task15InfrastructureException("reader failed after " + number + " record(s)", e);
        }
        return courses;
    }
}
