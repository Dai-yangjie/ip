package ev.command;

import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

public class FindCommand extends Command {

    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(keyword, tasks);
    }
}
