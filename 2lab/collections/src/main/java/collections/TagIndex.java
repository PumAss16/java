package collections;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class TagIndex {

    private final Map<Tag, Set<CourseId>> index = new LinkedHashMap<>();

    public void add(Course course) {
        for (Tag tag : course.tags()) {
            Set<CourseId> ids = index.computeIfAbsent(tag, t -> new LinkedHashSet<>());
            if (!ids.contains(course.id())) {
                ids.add(course.id());
            }
        }
    }

    public void remove(Course course) {
        for (Tag tag : course.tags()) {
            Set<CourseId> ids = index.get(tag);
            if (ids != null) {
                ids.remove(course.id());
                if (ids.isEmpty()) {
                    index.remove(tag);
                }
            }
        }
    }

    public Set<CourseId> byTag(Tag tag) {
        Set<CourseId> ids = index.get(tag);
        if (ids == null) {
            return Set.of();
        }
        return Collections.unmodifiableSet(new LinkedHashSet<>(ids));
    }

    public Set<CourseId> byAllTags(Tag... tags) {
        Set<CourseId> result = new LinkedHashSet<>();
        if (tags.length == 0) {
            return result;
        }
        Set<CourseId> smallest = null;
        for (Tag tag : tags) {
            Set<CourseId> ids = index.get(tag);
            if (ids == null) {
                return result;
            }
            if (smallest == null || ids.size() < smallest.size()) {
                smallest = ids;
            }
        }
        result.addAll(smallest);
        for (Tag tag : tags) {
            result.retainAll(index.get(tag));
        }
        return result;
    }

    public Set<Tag> tags() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(index.keySet()));
    }

    public int size() {
        return index.size();
    }
}
