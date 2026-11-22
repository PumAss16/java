package coursehub.practice3.repository;

import coursehub.practice3.domain.Task00Course;
import java.util.List;
import java.util.Optional;

public interface Task08CourseReader {

    Optional<Task00Course> findById(String code);

    List<Task00Course> findAll();
}
