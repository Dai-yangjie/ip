package ev;

import java.nio.file.Path;
import java.nio.file.Paths;

import ev.command.Command;
import ev.parser.Parser;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

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

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String line = ui.readCommand();
            if (line.isEmpty()) {
                continue;
            }
            try {
                Command command = Parser.parse(line);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
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
}
