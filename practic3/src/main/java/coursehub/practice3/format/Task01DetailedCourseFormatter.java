package coursehub.practice3.format;

import coursehub.practice3.domain.Task00Course;

public final class Task01DetailedCourseFormatter implements Task01CourseFormatter {

    @Override
    public String format(Task00Course course) {
        return course.code()
                + " | " + course.title()
                + " | age " + course.minAge() + "+"
                + " | seats " + course.capacity()
                + " | prerequisites " + course.prerequisites()
                + " | slot " + course.scheduleSlot();
    }
}
