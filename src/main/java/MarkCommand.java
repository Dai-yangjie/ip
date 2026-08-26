public class MarkCommand extends Command {

    private final int taskNumber;
    private final boolean isDone;

    public MarkCommand(int taskNumber, boolean isDone) {
        this.taskNumber = taskNumber;
        this.isDone = isDone;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EVException {
        Task task = tasks.getByNumber(taskNumber);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        ui.showMarked(task, isDone);
        storage.save(tasks.asList());
    }
}
