package coursehub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Граница импорта (задания 6 и 14).
 * Переводит NumberFormatException в CourseImportException с номером записи и cause,
 * ловит только конкретные ожидаемые типы: баг парсера проходит насквозь.
 * Источник читается в try-with-resources, результат возвращается только
 * после полного успеха — частично опубликованного результата не бывает.
 */
public class CourseImporter {

    private final CourseParser parser = new CourseParser();

    public Course parseCourse(String line, int recordNumber) throws CourseImportException {
        try {
            return parser.parse(line);
        } catch (NumberFormatException | ValidationException e) {
            throw new CourseImportException(recordNumber, "malformed record #" + recordNumber, e);
        }
    }

    public List<Course> importAll(RecordReader reader) throws CourseImportException {
        List<Course> courses = new ArrayList<>();
        int number = 0;
        try (reader) {
            String line;
            while ((line = reader.readRecord()) != null) {
                courses.add(parseCourse(line, ++number));
            }
        } catch (IOException e) {
            throw new InfrastructureException("reader failed after " + number + " record(s)", e);
        }
        return courses;
    }
}
