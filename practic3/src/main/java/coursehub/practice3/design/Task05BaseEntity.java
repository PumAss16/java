package coursehub.practice3.design;

import java.util.Objects;

public abstract class Task05BaseEntity implements Task05Identifiable {

    private final String id;

    protected Task05BaseEntity(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        this.id = id;
    }

    @Override
    public final String id() {
        return id;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Task05BaseEntity that = (Task05BaseEntity) other;
        return id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }
}
