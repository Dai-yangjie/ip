import java.time.LocalDate;

public class Parser {

    private static final String OPTION_BY = "/by";
    private static final String OPTION_FROM = "/from";
    private static final String OPTION_TO = "/to";

    private static final String DEADLINE_USAGE =
            "Try something like: deadline return book /by 2019-12-02 1800";

    private static final String EVENT_USAGE =
            "Try something like: event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";

    private static final String ON_USAGE = "Try something like: on 2019-12-02";

    public record ParsedCommand(Command command, String argument) {
    }

    public static ParsedCommand parse(String line) throws EVException {
        String[] parts = line.split(" ", 2);
        Command command = Command.fromKeyword(parts[0]);
        String argument = parts.length > 1 ? parts[1].trim() : "";
        return new ParsedCommand(command, argument);
    }

    public static Todo parseTodo(String argument) throws EVException {
        if (argument.isEmpty()) {
            throw new EVException("A todo needs a description.\n"
                    + "Try something like: todo borrow book");
        }
        return new Todo(argument);
    }

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

    public static LocalDate parseDate(String argument) throws EVException {
        if (argument.isEmpty()) {
            throw new EVException("Please tell me which date you are asking about.\n" + ON_USAGE);
        }
        return DateTimes.parse(argument).toLocalDate();
    }
}
