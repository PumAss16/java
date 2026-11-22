package coursehub.practice3.format;

import coursehub.practice3.domain.Task00Course;

public final class Task03MarkdownCourseFormatter implements Task01CourseFormatter {

    @Override
    public String format(Task00Course course) {
        return "- **" + course.code() + "** — " + course.title()
                + " (" + course.capacity() + " seats)";
    }
}
