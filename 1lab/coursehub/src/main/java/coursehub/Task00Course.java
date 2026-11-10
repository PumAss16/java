package coursehub;

public class Task00Course {

    public static final int MIN_HOURS = 4;
    public static final int MAX_HOURS = 160;

    private final Task10CourseCode code;
    private final String title;
    private final int hours;
    private final int capacity;
    private final Task10CourseCode prerequisite;

    private Task13CourseStatus status;
    private int enrolled;
    private int completed;

    public Task00Course(Task10CourseCode code, String title, int hours, int capacity, int enrolled,
                  Task13CourseStatus status, Task10CourseCode prerequisite) {
        if (code == null || title == null || title.isBlank()) {
            throw new Task02ValidationException("code and title are required");
        }
        checkHours(hours);
        if (capacity <= 0 || enrolled < 0 || enrolled > capacity) {
            throw new Task02ValidationException("bad capacity/enrolled: " + capacity + "/" + enrolled);
        }
        this.code = code;
        this.title = title;
        this.hours = hours;
        this.capacity = capacity;
        this.enrolled = enrolled;
        this.status = status == null ? Task13CourseStatus.DRAFT : status;
        this.prerequisite = prerequisite;
    }

    public static void checkHours(int hours) {
        if (hours < MIN_HOURS || hours > MAX_HOURS) {
            throw new Task02ValidationException("duration must be in [" + MIN_HOURS + ", " + MAX_HOURS + "], got " + hours);
        }
    }

    public Task10CourseCode code() {
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

    public Task13CourseStatus status() {
        return status;
    }

    public Task10CourseCode prerequisite() {
        return prerequisite;
    }

    public boolean hasFreeSeat() {
        return enrolled < capacity;
    }

    public int completeHours(int h) {
        if (status != Task13CourseStatus.ACTIVE) {
            throw new Task02ValidationException("course is not ACTIVE: " + status);
        }
        if (h <= 0) {
            throw new Task02ValidationException("hours must be positive, got " + h);
        }
        if (completed + h > hours) {
            throw new Task02ValidationException("cannot complete " + h + "h, " + (hours - completed) + "h left");
        }
        completed += h;
        return completed;
    }

    public void reserveSeat() {
        if (!hasFreeSeat()) {
            throw new Task02ValidationException("no free seats on " + code.value());
        }
        enrolled++;
    }
}
