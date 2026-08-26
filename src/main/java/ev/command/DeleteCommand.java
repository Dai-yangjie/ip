package ev.command;

import ev.EVException;
import ev.storage.Storage;
import ev.task.Task;
import ev.task.TaskList;
import ev.ui.Ui;

public class DeleteCommand extends Command {

    private final int taskNumber;

    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EVException {
        Task removed = tasks.removeByNumber(taskNumber);
        ui.showRemoved(removed, tasks);
        storage.save(tasks.asList());
    }
}
