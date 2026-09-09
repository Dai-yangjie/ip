package ev.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ev.DateTimes;
import ev.EvException;

/**
 * A task that runs from one date and time to another.
 */
public class Event extends Task {

    /** One-letter type of this task in the save file. */
    public static final String TYPE = "E";

    /** Option the user types, both to set and to change when the event starts. */
    public static final String OPTION_FROM = "/from";

    /** Option the user types, both to set and to change when the event ends. */
    public static final String OPTION_TO = "/to";

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
    public void applyUpdate(String option, String value) throws EvException {
        if (option.equals(OPTION_FROM)) {
            from = DateTimes.parse(value);
            return;
        }
        if (option.equals(OPTION_TO)) {
            to = DateTimes.parse(value);
            return;
        }
        super.applyUpdate(option, value);
    }

    @Override
    protected String getTypeName() {
        return "an event";
    }

    @Override
    protected String listUpdatableOptions() {
        return super.listUpdatableOptions() + ", " + OPTION_FROM + ", " + OPTION_TO;
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
