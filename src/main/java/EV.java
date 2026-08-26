import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class EV {

    private static final Path DATA_FILE = Paths.get("data", "duke.txt");

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    public EV(Path dataFile) {
        ui = new Ui();
        storage = new Storage(dataFile);
        tasks = new TaskList();
    }

    public static void main(String[] args) {
        new EV(DATA_FILE).run();
    }

    public void run() {
        ui.showBanner();
        ui.showWelcome();
        loadTasks();

        while (ui.hasNextCommand()) {
            String line = ui.readCommand();
            if (line.isEmpty()) {
                continue;
            }
            try {
                Parser.ParsedCommand parsed = Parser.parse(line);
                if (parsed.command() == Command.BYE) {
                    break;
                }
                execute(parsed.command(), parsed.argument());
            } catch (EVException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.showFarewell();
    }

    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (EVException e) {
            ui.showError(e.getMessage());
            return;
        }
        int skipped = storage.getSkippedLineCount();
        if (skipped > 0) {
            ui.showSkippedLines(skipped, storage.getFile());
        }
    }

    private void execute(Command command, String argument) throws EVException {
        switch (command) {
        case LIST -> ui.showTasks(tasks);
        case ON -> listTasksOn(argument);
        case MARK -> setTaskDone(argument, true);
        case UNMARK -> setTaskDone(argument, false);
        case TODO -> addTask(Parser.parseTodo(argument));
        case DEADLINE -> addTask(Parser.parseDeadline(argument));
        case EVENT -> addTask(Parser.parseEvent(argument));
        case DELETE -> deleteTask(argument);
        default -> throw new AssertionError("Unhandled command: " + command);
        }
    }

    private void addTask(Task task) {
        tasks.add(task);
        ui.showAdded(task, tasks);
        saveTasks();
    }

    private void deleteTask(String argument) throws EVException {
        Task removed = tasks.removeByNumber(Parser.parseTaskNumber(argument));
        ui.showRemoved(removed, tasks);
        saveTasks();
    }

    private void setTaskDone(String argument, boolean isDone) throws EVException {
        Task task = tasks.getByNumber(Parser.parseTaskNumber(argument));
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        ui.showMarked(task, isDone);
        saveTasks();
    }

    private void listTasksOn(String argument) throws EVException {
        LocalDate date = Parser.parseDate(argument);
        ui.showTasksOn(date, tasks);
    }

    private void saveTasks() {
        try {
            storage.save(tasks.asList());
        } catch (EVException e) {
            ui.showError(e.getMessage());
        }
    }
}
