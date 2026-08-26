package ev.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ev.DateTimes;

/**
 * A task that runs from one date and time to another.
 */
public class Event extends Task {

    /** One-letter type of this task in the save file. */
    public static final String TYPE = "E";

    /** When the event starts. */
    protected LocalDateTime from;

    /** When the event ends. */
    protected LocalDateTime to;

    /**
     * Creates an event that is not done yet.
     *
     * @param description what the user typed as the description of the task.
     * @param from when the event starts.
     * @param to when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether this event is running on the given date.
     *
     * <p>An event that spans several days counts as happening on its first day,
     * its last day and every day in between.
     *
     * @param date the date being asked about.
     * @return true if the event covers that date.
     */
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
