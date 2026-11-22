package coursehub.practice3.design;

public final class Task05Instructor extends Task05BaseEntity {

    private final String name;

    public Task05Instructor(String id, String name) {
        super(id);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    public String name() {
        return name;
    }
}
