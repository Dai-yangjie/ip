package ev.command;

import ev.EvException;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

public abstract class Command {

    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws EvException;

    public boolean isExit() {
        return false;
    }
}
