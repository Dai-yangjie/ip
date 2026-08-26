package ev.command;

import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

public class ListCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
