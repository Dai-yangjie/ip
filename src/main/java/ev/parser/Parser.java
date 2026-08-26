package ev.parser;

import java.time.LocalDate;

import ev.DateTimes;
import ev.EVException;
import ev.command.AddCommand;
import ev.command.Command;
import ev.command.DeleteCommand;
import ev.command.ExitCommand;
import ev.command.ListCommand;
import ev.command.MarkCommand;
import ev.command.OnCommand;
import ev.task.Deadline;
import ev.task.Event;
import ev.task.Todo;

/**
 * Turns a line the user typed into a command that is ready to run.
 *
 * <p>Everything about the shape of the input lives here: which keyword starts a command,
 * where {@code /by}, {@code /from} and {@code /to} split an argument, and what counts as
 * a task number or a date. A command that comes out of this class needs no further
 * checking, and the messages thrown along the way show the user what to type instead.
 */
public class Parser {

    private static final String OPTION_BY = "/by";
    private static final String OPTION_FROM = "/from";
    private static final String OPTION_TO = "/to";

    private static final String DEADLINE_USAGE =
            "Try something like: deadline return book /by 2019-12-02 1800";

    private static final String EVENT_USAGE =
            "Try something like: event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";

    private static final String ON_USAGE = "Try something like: on 2019-12-02";

    /**
     * Returns the command the given line asks for.
     *
     * @param line one line of input, already trimmed and not empty.
     * @return a command holding everything it needs to run.
     * @throws EVException if the keyword is unknown or the rest of the line is not usable.
     */
    public static Command parse(String line) throws EVException {
        String[] parts = line.split(" ", 2);
        CommandWord word = CommandWord.fromKeyword(parts[0]);
        String argument = parts.length > 1 ? parts[1].trim() : "";

        return switch (word) {
        case TODO -> new AddCommand(parseTodo(argument));
        case DEADLINE -> new AddCommand(parseDeadline(argument));
        case EVENT -> new AddCommand(parseEvent(argument));
        case LIST -> new ListCommand();
        case ON -> new OnCommand(parseDate(argument));
        case MARK -> new MarkCommand(parseTaskNumber(argument), true);
        case UNMARK -> new MarkCommand(parseTaskNumber(argument), false);
        case DELETE -> new DeleteCommand(parseTaskNumber(argument));
        case BYE -> new ExitCommand();
        };
    }

    /**
     * Builds a todo from the words that follow the keyword.
     *
     * @param argument everything after {@code todo}.
     * @return the new task.
     * @throws EVException if no description was given.
     */
    public static Todo parseTodo(String argument) throws EVException {
        if (argument.isEmpty()) {
            throw new EVException("A todo needs a description.\n"
                    + "Try something like: todo borrow book");
        }
        return new Todo(argument);
    }

    /**
     * Builds a deadline from a description followed by {@code /by} and a due date.
     *
     * @param argument everything after {@code deadline}.
     * @return the new task.
     * @throws EVException if {@code /by} is missing, either part is empty, or the date cannot be read.
     */
    public static Deadline parseDeadline(String argument) throws EVException {
        int byIndex = argument.indexOf(OPTION_BY);
        if (byIndex < 0) {
            throw new EVException("A deadline needs a " + OPTION_BY + " to say when it is due.\n"
                    + DEADLINE_USAGE);
        }
        String description = argument.substring(0, byIndex).trim();
        String by = argument.substring(byIndex + OPTION_BY.length()).trim();
        if (description.isEmpty()) {
            throw new EVException("A deadline needs a description before " + OPTION_BY + ".\n"
                    + DEADLINE_USAGE);
        }
        if (by.isEmpty()) {
            throw new EVException("A deadline needs a due time after " + OPTION_BY + ".\n"
                    + DEADLINE_USAGE);
        }
        return new Deadline(description, DateTimes.parse(by));
    }

    /**
     * Builds an event from a description followed by {@code /from} and {@code /to}.
     *
     * @param argument everything after {@code event}.
     * @return the new task.
     * @throws EVException if either option is missing or out of order, a part is empty,
     *     or a date cannot be read.
     */
    public static Event parseEvent(String argument) throws EVException {
        int fromIndex = argument.indexOf(OPTION_FROM);
        int toIndex = argument.indexOf(OPTION_TO);
        if (fromIndex < 0) {
            throw new EVException("An event needs a " + OPTION_FROM + " to say when it starts.\n"
                    + EVENT_USAGE);
        }
        if (toIndex < 0) {
            throw new EVException("An event needs a " + OPTION_TO + " to say when it ends.\n"
                    + EVENT_USAGE);
        }
        if (toIndex < fromIndex) {
            throw new EVException("Please put " + OPTION_FROM + " before " + OPTION_TO + ".\n"
                    + EVENT_USAGE);
        }
        String description = argument.substring(0, fromIndex).trim();
        String from = argument.substring(fromIndex + OPTION_FROM.length(), toIndex).trim();
        String to = argument.substring(toIndex + OPTION_TO.length()).trim();
        if (description.isEmpty()) {
            throw new EVException("An event needs a description before " + OPTION_FROM + ".\n"
                    + EVENT_USAGE);
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new EVException("An event needs a start time and an end time.\n"
                    + EVENT_USAGE);
        }
        return new Event(description, DateTimes.parse(from), DateTimes.parse(to));
    }

    /**
     * Reads the task number a command such as {@code mark} was given.
     *
     * <p>Only the shape of the number is checked here. Whether a task with that number
     * exists is for the task list to say.
     *
     * @param argument everything after the keyword.
     * @return the number the user typed, counting from 1.
     * @throws EVException if nothing was given or it is not a whole number.
     */
    public static int parseTaskNumber(String argument) throws EVException {
        if (argument.isEmpty()) {
            throw new EVException("Please tell me which task number.\n"
                    + "Try something like: mark 2");
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new EVException("\"" + argument + "\" is not a task number.\n"
                    + "Try something like: mark 2");
        }
    }

    /**
     * Reads the date the {@code on} command was given, ignoring any time of day.
     *
     * @param argument everything after {@code on}.
     * @return the date asked about.
     * @throws EVException if nothing was given or the date cannot be read.
     */
    public static LocalDate parseDate(String argument) throws EVException {
        if (argument.isEmpty()) {
            throw new EVException("Please tell me which date you are asking about.\n" + ON_USAGE);
        }
        return DateTimes.parse(argument).toLocalDate();
    }
}
