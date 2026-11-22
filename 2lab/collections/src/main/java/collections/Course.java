package collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Course {

    private final CourseId id;
    private final CourseCode code;
    private final String title;
    private final Set<Tag> tags;
    private int views;

    public Course(CourseId id, CourseCode code, String title, Tag... tags) {
        this(id, code, title, new LinkedHashSet<>(Arrays.asList(tags)));
    }

    public Course(CourseId id, CourseCode code, String title, Set<Tag> tags) {
        if (id == null || code == null || title == null || title.isBlank() || tags == null) {
            throw new IllegalArgumentException("id, code, title, tags are required");
        }
        this.id = id;
        this.code = code;
        this.title = title;
        this.tags = Collections.unmodifiableSet(new LinkedHashSet<>(tags));
    }

    public CourseId id() {
        return id;
    }

    public CourseCode code() {
        return code;
    }

    public String title() {
        return title;
    }

    public Set<Tag> tags() {
        return tags;
    }

    public int views() {
        return views;
    }

    public void addView() {
        views++;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Course c && id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + "(" + code + ")";
    }
}
