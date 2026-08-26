package ev.command;

import java.time.LocalDate;

import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

public class OnCommand extends Command {

    private final LocalDate date;

    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date, tasks);
    }
}
