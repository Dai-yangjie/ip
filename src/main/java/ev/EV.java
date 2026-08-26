package ev;

import java.nio.file.Path;
import java.nio.file.Paths;

import ev.command.Command;
import ev.parser.Parser;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Entry point of the EV chatbot.
 *
 * <p>EV keeps a list of tasks that survives between runs. This class only wires the
 * parts together: it reads a line through the {@link Ui}, hands it to the {@link Parser},
 * and lets the resulting {@link Command} change the {@link TaskList} and report back.
 */
public class EV {

    /** Where the task list is saved, relative to the folder the app is run from. */
    private static final Path DATA_FILE = Paths.get("data", "duke.txt");

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a chatbot that saves to and loads from the given file.
     *
     * @param dataFile the save file. It does not have to exist yet.
     */
    public EV(Path dataFile) {
        ui = new Ui();
        storage = new Storage(dataFile);
        tasks = new TaskList();
    }

    /**
     * Starts the chatbot.
     *
     * @param args command line arguments, which are not used.
     */
    public static void main(String[] args) {
        new EV(DATA_FILE).run();
    }

    /**
     * Greets the user, loads the saved tasks, then handles commands until the user
     * says bye or the input runs out.
     */
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

    /**
     * Loads the saved tasks, starting with an empty list if the file cannot be read.
     * Lines that were skipped because they are not in the expected format are reported.
     */
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
