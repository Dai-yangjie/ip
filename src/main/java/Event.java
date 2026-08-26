import java.time.LocalDate;
import java.time.LocalDateTime;

public class Event extends Task {

    public static final String TYPE = "E";

    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    @Override
    public String toFileFormat() {
        return encode(TYPE, DateTimes.toFileFormat(from), DateTimes.toFileFormat(to));
    }

    @Override
    public String toString() {
        return "[" + TYPE + "]" + super.toString()
                + " (from: " + DateTimes.format(from) + " to: " + DateTimes.format(to) + ")";
    }
}
