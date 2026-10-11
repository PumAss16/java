package collections;

public record Tag(String name) {

    public Tag {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("tag required");
        }
        name = name.trim().toLowerCase();
    }

    public static Tag of(String raw) {
        return new Tag(raw);
    }

    @Override
    public String toString() {
        return name;
    }
}
