package coursehub;

/**
 * Низкоуровневый разбор записи "CS-101;Java;30;40" (задание 6).
 * Ничего не знает про номер записи, поэтому бросает NumberFormatException
 * без контекста. Ошибка формата == ожидаемая, баг (null) == исключение.
 */
public class CourseParser {

    public Course parse(String line) {
        String[] parts = line.split(";");
        if (parts.length != 4) {
            throw new NumberFormatException("expected 4 fields, got " + parts.length);
        }
        return new Course(CourseCode.of(parts[0].trim()), parts[1].trim(),
                Integer.parseInt(parts[3].trim()), Integer.parseInt(parts[2].trim()),
                0, CourseStatus.DRAFT, null);
    }
}
