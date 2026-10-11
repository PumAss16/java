package coursehub;

/** Курс: продолжительность, места, статус, prerequisite (задания 8, 9, 13). */
public class Course {

    public static final int MIN_HOURS = 4;
    public static final int MAX_HOURS = 160;

    private final CourseCode code;
    private final String title;
    private final int hours;
    private final int capacity;
    private final CourseCode prerequisite;

    private CourseStatus status;
    private int enrolled;
    private int completed;

    public Course(CourseCode code, String title, int hours, int capacity, int enrolled,
                  CourseStatus status, CourseCode prerequisite) {
        if (code == null || title == null || title.isBlank()) {
            throw new ValidationException("code and title are required");
        }
        checkHours(hours);
        if (capacity <= 0 || enrolled < 0 || enrolled > capacity) {
            throw new ValidationException("bad capacity/enrolled: " + capacity + "/" + enrolled);
        }
        this.code = code;
        this.title = title;
        this.hours = hours;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.status = status == null ? CourseStatus.DRAFT : status;
        this.prerequisite = prerequisite;
    }

    /** Границы продолжительности курса: [4, 160] часов (задание 9). */
    public static void checkHours(int hours) {
        if (hours < MIN_HOURS || hours > MAX_HOURS) {
            throw new ValidationException("duration must be in [" + MIN_HOURS + ", " + MAX_HOURS + "], got " + hours);
        }
    }

    public CourseCode code() {
        return code;
    }

    public String title() {
        return title;
    }

    public int hours() {
        return hours;
    }

    public int capacity() {
        return capacity;
    }

    public int enrolled() {
        return enrolled;
    }

    public int completed() {
        return completed;
    }

    public CourseStatus status() {
        return status;
    }

    public CourseCode prerequisite() {
        return prerequisite;
    }

    public boolean hasFreeSeat() {
        return enrolled < capacity;
    }

    /** Отмечает пройденные часы (задание 8). */
    public int completeHours(int h) {
        if (status != CourseStatus.ACTIVE) {
            throw new ValidationException("course is not ACTIVE: " + status);
        }
        if (h <= 0) {
            throw new ValidationException("hours must be positive, got " + h);
        }
        if (completed + h > hours) {
            throw new ValidationException("cannot complete " + h + "h, " + (hours - completed) + "h left");
        }
        completed += h;
        return completed;
    }

    public void reserveSeat() {
        if (!hasFreeSeat()) {
            throw new ValidationException("no free seats on " + code.value());
        }
        enrolled++;
    }
}
