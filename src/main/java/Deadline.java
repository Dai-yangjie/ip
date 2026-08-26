import java.time.LocalDate;
import java.time.LocalDateTime;

public class Deadline extends Task {

    public static final String TYPE = "D";

    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toFileFormat() {
        return encode(TYPE, DateTimes.toFileFormat(by));
    }

    @Override
    public String toString() {
        return "[" + TYPE + "]" + super.toString() + " (by: " + DateTimes.format(by) + ")";
    }
}
