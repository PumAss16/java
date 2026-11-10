package coursehub;

public enum Task03RejectionReason {

    COURSE_NOT_FOUND("CH-2001"),
    STUDENT_NOT_FOUND("CH-2002"),
    COURSE_NOT_ACTIVE("CH-2003"),
    COURSE_FULL("CH-2004"),
    PREREQUISITE_NOT_MET("CH-2005"),
    ALREADY_ENROLLED("CH-2006");

    private final String code;

    Task03RejectionReason(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
