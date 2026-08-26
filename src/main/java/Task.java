import java.time.LocalDate;

public abstract class Task {

    public static final String FIELD_SEPARATOR = " | ";
    public static final String DONE_FLAG = "1";
    public static final String NOT_DONE_FLAG = "0";

    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    public boolean occursOn(LocalDate date) {
        return false;
    }

    public abstract String toFileFormat();

    protected String encode(String type, String... extraFields) {
        StringBuilder encoded = new StringBuilder(type);
        encoded.append(FIELD_SEPARATOR).append(isDone ? DONE_FLAG : NOT_DONE_FLAG);
        encoded.append(FIELD_SEPARATOR).append(description);
        for (String field : extraFields) {
            encoded.append(FIELD_SEPARATOR).append(field);
        }
        return encoded.toString();
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
