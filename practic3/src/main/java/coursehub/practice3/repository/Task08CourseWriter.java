package coursehub.practice3.repository;

import coursehub.practice3.domain.Task00Course;

public interface Task08CourseWriter {

    void save(Task00Course course);

    boolean deleteById(String code);
}
