package ev.ui;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

import ev.DateTimes;
import ev.task.Task;
import ev.task.TaskList;

/**
 * Everything the user sees and types.
 *
 * <p>Each reply is printed between two divider lines so that the session reads as a
 * series of separate answers. Keeping the wording here means a different front end only
 * has to replace this class.
 */
public class Ui {

    private static final String LINE = "____________________________________________________________";

    private static final String BANNER = " _______     __\n"
            + "|   ____|   /  \\\n"
            + "|  |__     |    |\n"
            + "|   __|    |    |\n"
            + "|  |____    \\  /\n"
            + "|_______|    \\/\n";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns whether there is another line of input waiting.
     *
     * @return false once the input has run out, for example at the end of a piped file.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next line the user typed.
     *
     * @return the line, with the spaces around it removed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the EV banner. */
    public void showBanner() {
        System.out.println(BANNER);
    }

    /** Greets the user. */
    public void showWelcome() {
        show("Hello! I'm EV.\nWhat can I do for you?");
    }

    /** Says goodbye to the user. */
    public void showFarewell() {
        show("Bye. Hope to see you again soon!");
    }

    /**
     * Shows something that went wrong.
     *
     * @param message the message of the exception, written for the user to read.
     */
    public void showError(String message) {
        show(message);
    }

    /**
     * Warns that part of the save file could not be read.
     *
     * @param count how many lines were skipped.
     * @param file the save file they were skipped in.
     */
    public void showSkippedLines(int count, Path file) {
        show("I skipped " + count + " line(s) in " + file
                + " because they were not in the format I expect.\n"
                + "The rest of your tasks were loaded, and the file will be tidied up on the next change.");
    }

    /**
     * Confirms that a task was added.
     *
     * @param task the task that was added.
     * @param tasks the list it went into, used to report the new size.
     */
    public void showAdded(Task task, TaskList tasks) {
        show("Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.describeSize() + " in the list.");
    }

    /**
     * Confirms that a task was removed.
     *
     * @param task the task that was removed.
     * @param tasks the list it came out of, used to report the new size.
     */
    public void showRemoved(Task task, TaskList tasks) {
        show("Noted. I've removed this task:\n  " + task
                + "\nNow you have " + tasks.describeSize() + " in the list.");
    }

    /**
     * Confirms that the done status of a task changed.
     *
     * @param task the task in its new state.
     * @param isDone true if the task was just marked as done.
     */
    public void showMarked(Task task, boolean isDone) {
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        show(message + "\n  " + task);
    }

    /**
     * Shows the whole list, numbered from 1, or says so when there is nothing in it.
     *
     * @param tasks the list to show.
     */
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

    /**
     * Shows the tasks that happen on one date.
     *
     * <p>Each task keeps the number it has in the full list, so it can be marked or
     * deleted straight away without listing everything first.
     *
     * @param date the date being asked about.
     * @param tasks the list to look through.
     */
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

    /**
     * Shows the tasks whose description contains the given text.
     *
     * <p>As with {@link #showTasksOn}, each task keeps the number it has in the full
     * list, so a task can be marked or deleted straight after finding it.
     *
     * @param keyword the text that was searched for.
     * @param tasks the list to look through.
     */
    public void showMatchingTasks(String keyword, TaskList tasks) {
        StringBuilder listing = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.hasKeyword(keyword)) {
                appendNumbered(listing, i, task);
            }
        }
        if (listing.length() == 0) {
            show("No task in your list has \"" + keyword + "\" in its description.");
            return;
        }
        show("Here are the matching tasks in your list:" + listing);
    }

    /**
     * Appends one numbered task to a listing being built.
     *
     * @param listing the text built so far.
     * @param index position of the task in the full list, counting from 0.
     * @param task the task to append.
     */
    private void appendNumbered(StringBuilder listing, int index, Task task) {
        listing.append("\n").append(index + 1).append(".").append(task);
    }

    /**
     * Prints one reply between two divider lines.
     *
     * @param message the text of the reply, which may span several lines.
     */
    private void show(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
