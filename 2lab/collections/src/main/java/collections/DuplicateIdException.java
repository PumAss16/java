package collections;

public class DuplicateIdException extends RuntimeException {

    private final CourseId id;

    public DuplicateIdException(CourseId id) {
        super("duplicate course id: " + id);
        this.id = id;
    }

    public CourseId id() {
        return id;
    }
}
