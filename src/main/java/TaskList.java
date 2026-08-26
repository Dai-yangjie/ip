import java.util.ArrayList;
import java.util.List;

public class TaskList {

    private final ArrayList<Task> tasks;

    public TaskList() {
        this(new ArrayList<>());
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public List<Task> asList() {
        return tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task getByNumber(int taskNumber) throws EVException {
        requireExistingNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    public Task removeByNumber(int taskNumber) throws EVException {
        requireExistingNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    public String describeSize() {
        return tasks.size() + (tasks.size() == 1 ? " task" : " tasks");
    }

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
