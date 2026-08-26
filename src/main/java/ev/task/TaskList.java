package ev.task;

import java.util.ArrayList;
import java.util.List;

import ev.EVException;

/**
 * The list of tasks the user is keeping.
 *
 * <p>Tasks are numbered from 1 in the order they were added, which is how the user
 * refers to them. This class owns those numbers, so it is also the place where an
 * out-of-range task number is rejected.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list holding the given tasks, typically the ones just loaded from disk.
     *
     * @param tasks the tasks to start with, in the order they should be numbered.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks at all.
     *
     * @return true if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at the given position, counting from 0.
     *
     * <p>This is meant for walking the whole list. Use {@link #getByNumber(int)} for a
     * number that came from the user.
     *
     * @param index position of the task, from 0 to {@code size() - 1}.
     * @return the task at that position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the tasks as a plain list, for code that only needs to read them in order.
     *
     * @return the backing list of tasks.
     */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task the user refers to by number.
     *
     * @param taskNumber the number shown next to the task, counting from 1.
     * @return the matching task.
     * @throws EVException if the list is empty or there is no task with that number.
     */
    public Task getByNumber(int taskNumber) throws EVException {
        requireExistingNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Removes the task the user refers to by number. The tasks after it move up by one.
     *
     * @param taskNumber the number shown next to the task, counting from 1.
     * @return the task that was removed.
     * @throws EVException if the list is empty or there is no task with that number.
     */
    public Task removeByNumber(int taskNumber) throws EVException {
        requireExistingNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the size of the list as words, with the noun in the right number.
     *
     * @return text such as {@code "1 task"} or {@code "3 tasks"}.
     */
    public String describeSize() {
        return tasks.size() + (tasks.size() == 1 ? " task" : " tasks");
    }

    /**
     * Checks that a task number the user gave actually points at a task.
     *
     * @param taskNumber the number to check, counting from 1.
     * @throws EVException with a message for the user if the number cannot be used.
     */
    private void requireExistingNumber(int taskNumber) throws EVException {
        if (tasks.isEmpty()) {
            throw new EVException("Your list is empty, so there is no task to update yet.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EVException("There is no task " + taskNumber + " in your list.\n"
                    + "You currently have " + describeSize()
                    + ", so please pick a number between 1 and " + tasks.size() + ".");
        }
    }
}
