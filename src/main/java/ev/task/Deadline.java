package ev.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ev.DateTimes;

/**
 * A task that has to be done by a certain date and time.
 */
public class Deadline extends Task {

    /** One-letter type of this task in the save file. */
    public static final String TYPE = "D";

    /** When the task is due. */
    protected LocalDateTime by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description what the user typed as the description of the task.
     * @param by when the task is due. A deadline given without a time is due at the start of that day.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns whether this deadline falls due on the given date, whatever the time of day.
     *
     * @param date the date being asked about.
     * @return true if the task is due on that date.
     */
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
