import java.util.Scanner;

public class EV {

    private static final String LINE = "____________________________________________________________";

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";

    private static final int MAX_TASKS = 100;

    private static final String BANNER = " _______     __\n"
            + "|   ____|   /  \\\n"
            + "|  |__     |    |\n"
            + "|   __|    |    |\n"
            + "|  |____    \\  /\n"
            + "|_______|    \\/\n";

    private static String[] tasks = new String[MAX_TASKS];
    private static boolean[] isDone = new boolean[MAX_TASKS];
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
            if (command.equals(COMMAND_BYE)) {
                break;
            } else if (command.equals(COMMAND_LIST)) {
                reply(formatTasks());
            } else if (command.startsWith(COMMAND_MARK + " ")) {
                markTask(command.substring(COMMAND_MARK.length() + 1).trim());
            } else {
                addTask(command);
            }
        }

        reply("Bye. Hope to see you again soon!");
    }

    private static void addTask(String task) {
        if (taskCount == MAX_TASKS) {
            reply("Sorry, I can only remember " + MAX_TASKS + " tasks.");
            return;
        }
        tasks[taskCount] = task;
        isDone[taskCount] = false;
        taskCount++;
        reply("added: " + task);
    }

    private static void markTask(String argument) {
        int index = parseTaskIndex(argument);
        if (index < 0) {
            return;
        }
        isDone[index] = true;
        reply("Nice! I've marked this task as done:\n  " + formatTask(index));
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

    private static String formatTask(int index) {
        String statusIcon = isDone[index] ? "X" : " ";
        return "[" + statusIcon + "] " + tasks[index];
    }

    private static String formatTasks() {
        if (taskCount == 0) {
            return "There is nothing in your list yet.";
        }
        StringBuilder formatted = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            formatted.append("\n").append(i + 1).append(".").append(formatTask(i));
        }
        return formatted.toString();
    }

    private static void reply(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
