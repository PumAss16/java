package coursehub.practice3.design;

@FunctionalInterface
public interface Task05Formatter<T> {

    String format(T value);
}
