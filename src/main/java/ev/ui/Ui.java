package ev.ui;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

import ev.DateTimes;
import ev.task.Task;
import ev.task.TaskList;

public class Ui {

    private static final String LINE = "____________________________________________________________";

    private static final String BANNER = " _______     __\n"
            + "|   ____|   /  \\\n"
            + "|  |__     |    |\n"
            + "|   __|    |    |\n"
            + "|  |____    \\  /\n"
            + "|_______|    \\/\n";

    private final Scanner scanner = new Scanner(System.in);

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }

    public void showBanner() {
        System.out.println(BANNER);
    }

    public void showWelcome() {
        show("Hello! I'm EV.\nWhat can I do for you?");
    }

    public void showFarewell() {
        show("Bye. Hope to see you again soon!");
    }

    public void showError(String message) {
        show(message);
    }

    public void showSkippedLines(int count, Path file) {
        show("I skipped " + count + " line(s) in " + file
                + " because they were not in the format I expect.\n"
                + "The rest of your tasks were loaded, and the file will be tidied up on the next change.");
    }

    public void showAdded(Task task, TaskList tasks) {
        show("Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.describeSize() + " in the list.");
    }

    public void showRemoved(Task task, TaskList tasks) {
        show("Noted. I've removed this task:\n  " + task
                + "\nNow you have " + tasks.describeSize() + " in the list.");
    }

    public void showMarked(Task task, boolean isDone) {
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        show(message + "\n  " + task);
    }

    public void showTasks(TaskList tasks) {
        if (tasks.isEmpty()) {
            show("There is nothing in your list yet.");
            return;
        }
        StringBuilder listing = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            appendNumbered(listing, i, tasks.get(i));
        }
        show(listing.toString());
    }

    public void showTasksOn(LocalDate date, TaskList tasks) {
        StringBuilder listing = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                appendNumbered(listing, i, task);
            }
        }
        if (listing.length() == 0) {
            show("There is nothing on " + DateTimes.format(date) + ".");
            return;
        }
        show("Here are the tasks on " + DateTimes.format(date) + ":" + listing);
    }

    private void appendNumbered(StringBuilder listing, int index, Task task) {
        listing.append("\n").append(index + 1).append(".").append(task);
    }

    private void show(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
