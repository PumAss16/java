package coursehub;

/** Причины отказа в зачислении со стабильными кодами для публичного слоя. */
public enum RejectionReason {

    COURSE_NOT_FOUND("CH-2001"),
    STUDENT_NOT_FOUND("CH-2002"),
    COURSE_NOT_ACTIVE("CH-2003"),
    COURSE_FULL("CH-2004"),
    PREREQUISITE_NOT_MET("CH-2005"),
    ALREADY_ENROLLED("CH-2006");

    private final String code;

    RejectionReason(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
