package ev.task;

import java.time.LocalDate;

import ev.EvException;

/**
 * A single item in the task list.
 *
 * <p>A task always has a description and a done status. Subclasses add whatever
 * else their kind of task needs, such as a due date, and decide how the task is
 * rendered and how it is written to the save file.
 */
public abstract class Task {

    /** Separator written between fields of a task in the save file. */
    public static final String FIELD_SEPARATOR = " | ";

    /** Value written in the save file for a task that is done. */
    public static final String DONE_FLAG = "1";

    /** Value written in the save file for a task that is not done yet. */
    public static final String NOT_DONE_FLAG = "0";

    /** Option the user types to change the description, which every task has. */
    public static final String OPTION_DESC = "/desc";

    /** What the user typed as the description of this task. */
    protected String description;

    /** Whether the task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description what the user typed as the description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the character shown inside the status box of this task.
     *
     * @return {@code "X"} if the task is done, a single space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done yet. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether the description of this task contains the given text.
     *
     * <p>The search ignores capitalisation and matches part of a word, so that
     * {@code boo} finds a task about a book. Only the description is searched.
     *
     * @param keyword the text the user is looking for.
     * @return true if the description contains it.
     */
    public boolean hasKeyword(String keyword) {
        assert keyword != null && !keyword.isEmpty() : "An empty keyword would match every task";
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns whether this task happens on the given date.
     *
     * <p>Tasks without a date never happen on any date, so this returns false
     * unless a subclass says otherwise.
     *
     * @param date the date being asked about.
     * @return true if the task falls on that date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Changes one detail of this task, leaving everything else as it is.
     *
     * <p>Every task accepts {@link #OPTION_DESC}. Subclasses accept the options that name
     * a field of their own and pass anything else back here to be rejected, so that a
     * caller never has to ask what kind of task it is holding.
     *
     * @param option the option the user typed, such as {@code /by}.
     * @param value the new value, already trimmed and not empty.
     * @throws EvException if this kind of task has no such field, or the value cannot be
     *     read. The task is left unchanged in both cases.
     */
    public void applyUpdate(String option, String value) throws EvException {
        if (option.equals(OPTION_DESC)) {
            description = value;
            return;
        }
        throw new EvException("You cannot change " + option + " on " + getTypeName() + ".",
                "On " + getTypeName() + " you can update: " + listUpdatableOptions());
    }

    /**
     * Returns how this kind of task is named in a sentence, article included.
     *
     * @return text such as {@code "a todo"} or {@code "an event"}.
     */
    protected abstract String getTypeName();

    /**
     * Returns the options this kind of task accepts, in the order they are shown.
     *
     * @return the options, separated by commas.
     */
    protected String listUpdatableOptions() {
        return OPTION_DESC;
    }

    /**
     * Returns this task as the single line that represents it in the save file.
     *
     * @return a line in the form {@code TYPE | STATUS | description | ...}.
     */
    public abstract String toFileFormat();

    /**
     * Builds a save file line from the parts every task shares plus any extra fields.
     *
     * @param type the one-letter type of the task, such as {@code "T"}.
     * @param extraFields fields the subclass adds after the description, in order.
     * @return the encoded line, with fields joined by {@link #FIELD_SEPARATOR}.
     */
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
