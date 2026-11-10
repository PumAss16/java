package coursehub;

import java.util.ArrayList;
import java.util.List;

public class Task15CourseHub {

    public record Response<T>(T value, String code, String message) {
        public boolean isOk() {
            return code == null;
        }
    }

    private final Task07EnrollmentService service;
    private final Task06CourseImporter importer;
    private final List<Throwable> errors = new ArrayList<>();

    public Task15CourseHub(Task07EnrollmentService service, Task06CourseImporter importer) {
        this.service = service;
        this.importer = importer;
    }

    public Response<Task00Course> enroll(String studentId, String courseCode) {
        try {
            return new Response<>(service.enroll(studentId, Task10CourseCode.of(courseCode)), null, "ok");
        } catch (Task03EnrollmentRejectedException e) {
            errors.add(e);
            return new Response<>(null, e.code(), "enrollment refused: " + e.reason());
        } catch (Task15InfrastructureException e) {
            errors.add(e);
            return new Response<>(null, e.code(), "service unavailable");
        }
    }

    public Response<List<Task00Course>> importCourses(Task14RecordReader reader) {
        try {
            return new Response<>(importer.importAll(reader), null, "ok");
        } catch (Task06CourseImportException e) {
            errors.add(e);
            return new Response<>(null, e.code(), "import failed at record " + e.recordNumber());
        } catch (Task15InfrastructureException e) {
            errors.add(e);
            return new Response<>(null, e.code(), "import failed: source unavailable");
        }
    }

    public List<Throwable> errors() {
        return List.copyOf(errors);
    }
}
