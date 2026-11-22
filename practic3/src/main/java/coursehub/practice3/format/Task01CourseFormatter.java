package coursehub.practice3.format;

import coursehub.practice3.design.Task05Formatter;
import coursehub.practice3.domain.Task00Course;

@FunctionalInterface
public interface Task01CourseFormatter extends Task05Formatter<Task00Course> {

    @Override
    String format(Task00Course course);
}
