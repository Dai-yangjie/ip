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

    private static final String OPTION_BY = "/by";
    private static final String OPTION_FROM = "/from";
    private static final String OPTION_TO = "/to";

    private static final int MAX_TASKS = 100;

    private static final String BANNER = " _______     __\n"
            + "|   ____|   /  \\\n"
            + "|  |__     |    |\n"
            + "|   __|    |    |\n"
            + "|  |____    \\  /\n"
            + "|_______|    \\/\n";

    private static Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

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
            } else if (keyword.equals(COMMAND_LIST)) {
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
            } else {
                reply("Sorry, I don't know what \"" + keyword + "\" means.\n"
                        + "Try: todo, deadline, event, list, mark, unmark or bye.");
            }
        }

        reply("Bye. Hope to see you again soon!");
    }

    private static void addTodo(String argument) {
        if (argument.isEmpty()) {
            reply("A todo needs a description, e.g. todo borrow book");
            return;
        }
        addTask(new Todo(argument));
    }

    private static void addDeadline(String argument) {
        int byIndex = argument.indexOf(OPTION_BY);
        if (byIndex < 0) {
            reply("A deadline needs a " + OPTION_BY + ", e.g. deadline return book /by Sunday");
            return;
        }
        String description = argument.substring(0, byIndex).trim();
        String by = argument.substring(byIndex + OPTION_BY.length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            reply("A deadline needs a description and a time, e.g. deadline return book /by Sunday");
            return;
        }
        addTask(new Deadline(description, by));
    }

    private static void addEvent(String argument) {
        int fromIndex = argument.indexOf(OPTION_FROM);
        int toIndex = argument.indexOf(OPTION_TO);
        if (fromIndex < 0 || toIndex < fromIndex) {
            reply("An event needs a " + OPTION_FROM + " and a " + OPTION_TO
                    + ", e.g. event project meeting /from Mon 2pm /to 4pm");
            return;
        }
        String description = argument.substring(0, fromIndex).trim();
        String from = argument.substring(fromIndex + OPTION_FROM.length(), toIndex).trim();
        String to = argument.substring(toIndex + OPTION_TO.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            reply("An event needs a description, a start and an end, "
                    + "e.g. event project meeting /from Mon 2pm /to 4pm");
            return;
        }
        addTask(new Event(description, from, to));
    }

    private static void addTask(Task task) {
        if (taskCount == MAX_TASKS) {
            reply("Sorry, I can only remember " + MAX_TASKS + " tasks.");
            return;
        }
        tasks[taskCount] = task;
        taskCount++;
        reply("Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " " + pluraliseTask(taskCount) + " in the list.");
    }

    private static void setTaskDone(String argument, boolean done) {
        int index = parseTaskIndex(argument);
        if (index < 0) {
            return;
        }
        Task task = tasks[index];
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

    private static int parseTaskIndex(String argument) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            reply("Please tell me which task number, e.g. mark 2");
            return -1;
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            reply("There is no task " + taskNumber + " in your list.");
            return -1;
        }
        return taskNumber - 1;
    }

    private static String formatTasks() {
        if (taskCount == 0) {
            return "There is nothing in your list yet.";
        }
        StringBuilder formatted = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            formatted.append("\n").append(i + 1).append(".").append(tasks[i]);
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
