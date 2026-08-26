public abstract class Command {

    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws EVException;

    public boolean isExit() {
        return false;
    }
}
