package ev.parser;

import java.time.LocalDate;

import ev.DateTimes;
import ev.EvException;
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

public class Parser {

    private static final String OPTION_BY = "/by";
    private static final String OPTION_FROM = "/from";
    private static final String OPTION_TO = "/to";

    private static final String DEADLINE_USAGE =
            "Try something like: deadline return book /by 2019-12-02 1800";

    private static final String EVENT_USAGE =
            "Try something like: event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";

    private static final String ON_USAGE = "Try something like: on 2019-12-02";

    public static Command parse(String line) throws EvException {
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

    public static Todo parseTodo(String argument) throws EvException {
        if (argument.isEmpty()) {
            throw new EvException("A todo needs a description.\n"
                    + "Try something like: todo borrow book");
        }
        return new Todo(argument);
    }

    public static Deadline parseDeadline(String argument) throws EvException {
        int byIndex = argument.indexOf(OPTION_BY);
        if (byIndex < 0) {
            throw new EvException("A deadline needs a " + OPTION_BY + " to say when it is due.\n"
                    + DEADLINE_USAGE);
        }
        String description = argument.substring(0, byIndex).trim();
        String by = argument.substring(byIndex + OPTION_BY.length()).trim();
        if (description.isEmpty()) {
            throw new EvException("A deadline needs a description before " + OPTION_BY + ".\n"
                    + DEADLINE_USAGE);
        }
        if (by.isEmpty()) {
            throw new EvException("A deadline needs a due time after " + OPTION_BY + ".\n"
                    + DEADLINE_USAGE);
        }
        return new Deadline(description, DateTimes.parse(by));
    }

    public static Event parseEvent(String argument) throws EvException {
        int fromIndex = argument.indexOf(OPTION_FROM);
        int toIndex = argument.indexOf(OPTION_TO);
        if (fromIndex < 0) {
            throw new EvException("An event needs a " + OPTION_FROM + " to say when it starts.\n"
                    + EVENT_USAGE);
        }
        if (toIndex < 0) {
            throw new EvException("An event needs a " + OPTION_TO + " to say when it ends.\n"
                    + EVENT_USAGE);
        }
        if (toIndex < fromIndex) {
            throw new EvException("Please put " + OPTION_FROM + " before " + OPTION_TO + ".\n"
                    + EVENT_USAGE);
        }
        String description = argument.substring(0, fromIndex).trim();
        String from = argument.substring(fromIndex + OPTION_FROM.length(), toIndex).trim();
        String to = argument.substring(toIndex + OPTION_TO.length()).trim();
        if (description.isEmpty()) {
            throw new EvException("An event needs a description before " + OPTION_FROM + ".\n"
                    + EVENT_USAGE);
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new EvException("An event needs a start time and an end time.\n"
                    + EVENT_USAGE);
        }
        return new Event(description, DateTimes.parse(from), DateTimes.parse(to));
    }

    public static int parseTaskNumber(String argument) throws EvException {
        if (argument.isEmpty()) {
            throw new EvException("Please tell me which task number.\n"
                    + "Try something like: mark 2");
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new EvException("\"" + argument + "\" is not a task number.\n"
                    + "Try something like: mark 2");
        }
    }

    public static LocalDate parseDate(String argument) throws EvException {
        if (argument.isEmpty()) {
            throw new EvException("Please tell me which date you are asking about.\n" + ON_USAGE);
        }
        return DateTimes.parse(argument).toLocalDate();
    }
}
