import java.util.ArrayList;
import java.util.Scanner;

public class EV {

    private static final String LINE = "____________________________________________________________";

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_DELETE = "delete";

    private static final String OPTION_BY = "/by";
    private static final String OPTION_FROM = "/from";
    private static final String OPTION_TO = "/to";

    private static final String BANNER = " _______     __\n"
            + "|   ____|   /  \\\n"
            + "|  |__     |    |\n"
            + "|   __|    |    |\n"
            + "|  |____    \\  /\n"
            + "|_______|    \\/\n";

    private static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println(BANNER);
        reply("Hello! I'm EV.\nWhat can I do for you?");

        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String command = in.nextLine().trim();
            if (command.isEmpty()) {
                continue;
            }

            String[] parts = command.split(" ", 2);
            String keyword = parts[0];
            String argument = parts.length > 1 ? parts[1].trim() : "";

            if (keyword.equals(COMMAND_BYE)) {
                break;
            }

            try {
                handleCommand(keyword, argument);
            } catch (EVException e) {
                reply(e.getMessage());
            }
        }

        reply("Bye. Hope to see you again soon!");
    }

    private static void handleCommand(String keyword, String argument) throws EVException {
        if (keyword.equals(COMMAND_LIST)) {
            reply(formatTasks());
        } else if (keyword.equals(COMMAND_MARK)) {
            setTaskDone(argument, true);
        } else if (keyword.equals(COMMAND_UNMARK)) {
            setTaskDone(argument, false);
        } else if (keyword.equals(COMMAND_TODO)) {
            addTodo(argument);
        } else if (keyword.equals(COMMAND_DEADLINE)) {
            addDeadline(argument);
        } else if (keyword.equals(COMMAND_EVENT)) {
            addEvent(argument);
        } else if (keyword.equals(COMMAND_DELETE)) {
            deleteTask(argument);
        } else {
            throw new EVException("I don't know what \"" + keyword + "\" means.\n"
                    + "I understand: todo, deadline, event, list, mark, unmark, delete, bye.");
        }
    }

    private static void addTodo(String argument) throws EVException {
        if (argument.isEmpty()) {
            throw new EVException("A todo needs a description.\n"
                    + "Try something like: todo borrow book");
        }
        addTask(new Todo(argument));
    }

    private static void addDeadline(String argument) throws EVException {
        int byIndex = argument.indexOf(OPTION_BY);
        if (byIndex < 0) {
            throw new EVException("A deadline needs a " + OPTION_BY + " to say when it is due.\n"
                    + "Try something like: deadline return book /by Sunday");
        }
        String description = argument.substring(0, byIndex).trim();
        String by = argument.substring(byIndex + OPTION_BY.length()).trim();
        if (description.isEmpty()) {
            throw new EVException("A deadline needs a description before " + OPTION_BY + ".\n"
                    + "Try something like: deadline return book /by Sunday");
        }
        if (by.isEmpty()) {
            throw new EVException("A deadline needs a due time after " + OPTION_BY + ".\n"
                    + "Try something like: deadline return book /by Sunday");
        }
        addTask(new Deadline(description, by));
    }

    private static void addEvent(String argument) throws EVException {
        int fromIndex = argument.indexOf(OPTION_FROM);
        int toIndex = argument.indexOf(OPTION_TO);
        if (fromIndex < 0) {
            throw new EVException("An event needs a " + OPTION_FROM + " to say when it starts.\n"
                    + "Try something like: event project meeting /from Mon 2pm /to 4pm");
        }
        if (toIndex < 0) {
            throw new EVException("An event needs a " + OPTION_TO + " to say when it ends.\n"
                    + "Try something like: event project meeting /from Mon 2pm /to 4pm");
        }
        if (toIndex < fromIndex) {
            throw new EVException("Please put " + OPTION_FROM + " before " + OPTION_TO + ".\n"
                    + "Try something like: event project meeting /from Mon 2pm /to 4pm");
        }
        String description = argument.substring(0, fromIndex).trim();
        String from = argument.substring(fromIndex + OPTION_FROM.length(), toIndex).trim();
        String to = argument.substring(toIndex + OPTION_TO.length()).trim();
        if (description.isEmpty()) {
            throw new EVException("An event needs a description before " + OPTION_FROM + ".\n"
                    + "Try something like: event project meeting /from Mon 2pm /to 4pm");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new EVException("An event needs a start time and an end time.\n"
                    + "Try something like: event project meeting /from Mon 2pm /to 4pm");
        }
        addTask(new Event(description, from, to));
    }

    private static void addTask(Task task) {
        tasks.add(task);
        reply("Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " " + pluraliseTask(tasks.size()) + " in the list.");
    }

    private static void deleteTask(String argument) throws EVException {
        Task removed = tasks.remove(parseTaskIndex(argument));
        reply("Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " " + pluraliseTask(tasks.size()) + " in the list.");
    }

    private static void setTaskDone(String argument, boolean done) throws EVException {
        Task task = tasks.get(parseTaskIndex(argument));
        if (done) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        String message = done
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        reply(message + "\n  " + task);
    }

    private static int parseTaskIndex(String argument) throws EVException {
        if (argument.isEmpty()) {
            throw new EVException("Please tell me which task number.\n"
                    + "Try something like: mark 2");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new EVException("\"" + argument + "\" is not a task number.\n"
                    + "Try something like: mark 2");
        }
        if (tasks.isEmpty()) {
            throw new EVException("Your list is empty, so there is no task to update yet.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EVException("There is no task " + taskNumber + " in your list.\n"
                    + "You currently have " + tasks.size() + " " + pluraliseTask(tasks.size())
                    + ", so please pick a number between 1 and " + tasks.size() + ".");
        }
        return taskNumber - 1;
    }

    private static String formatTasks() {
        if (tasks.isEmpty()) {
            return "There is nothing in your list yet.";
        }
        StringBuilder formatted = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            formatted.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return formatted.toString();
    }

    private static String pluraliseTask(int count) {
        return count == 1 ? "task" : "tasks";
    }

    private static void reply(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
