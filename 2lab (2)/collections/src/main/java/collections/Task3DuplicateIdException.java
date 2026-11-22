package collections;

class DuplicateIdException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final CourseId id;

    public DuplicateIdException(CourseId id) {
        super("duplicate course id: " + id);
        this.id = id;
    }

    public CourseId id() {
        return id;
    }
}
