package coursehub.practice3.defaults;

public interface Task10Auditable {

    default String describe() {
        return "auditable";
    }
}
