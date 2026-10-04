package coursehub;

import java.util.ArrayList;
import java.util.List;

/**
 * Публичная граница CourseHub (задание 15): наружу уходят только стабильные коды.
 *
 * Ловятся ровно три типа:
 *   EnrollmentRejectedException и CourseImportException — ожидаемые отказы;
 *   InfrastructureException — сбой инфраструктуры.
 * Ошибка программиста (ValidationException, NullPointerException) и Error
 * проходят насквозь: catch (Exception) превратил бы баг в «отказ пользователю»,
 * спрятал бы дефект из мониторинга и смешал бы бизнес-отказ с поломкой.
 * Cause сохраняется в журнале диагностики, но клиенту не отдаётся.
 */
public class CourseHub {

    /** Ответ публичного слоя: либо значение, либо код ошибки. */
    public record Response<T>(T value, String code, String message) {
        public boolean isOk() {
            return code == null;
        }
    }

    private final EnrollmentService service;
    private final CourseImporter importer;
    private final List<Throwable> errors = new ArrayList<>();

    public CourseHub(EnrollmentService service, CourseImporter importer) {
        this.service = service;
        this.importer = importer;
    }

    public Response<Course> enroll(String studentId, String courseCode) {
        try {
            return new Response<>(service.enroll(studentId, CourseCode.of(courseCode)), null, "ok");
        } catch (EnrollmentRejectedException e) {
            errors.add(e);
            return new Response<>(null, e.code(), "enrollment refused: " + e.reason());
        } catch (InfrastructureException e) {
            errors.add(e);
            return new Response<>(null, e.code(), "service unavailable");
        }
    }

    public Response<List<Course>> importCourses(RecordReader reader) {
        try {
            return new Response<>(importer.importAll(reader), null, "ok");
        } catch (CourseImportException e) {
            errors.add(e);
            return new Response<>(null, e.code(), "import failed at record " + e.recordNumber());
        } catch (InfrastructureException e) {
            errors.add(e);
            return new Response<>(null, e.code(), "import failed: source unavailable");
        }
    }

    /** Журнал диагностики: исключение целиком, вместе с cause и suppressed. */
    public List<Throwable> errors() {
        return List.copyOf(errors);
    }
}
