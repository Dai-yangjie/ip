package ev.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ev.DateTimes;
import ev.EVException;
import ev.task.Deadline;
import ev.task.Event;
import ev.task.Task;
import ev.task.Todo;

/**
 * Reads the task list from disk and writes it back.
 *
 * <p>The save file holds one task per line, with its fields separated by a pipe:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2019-12-02T18:00
 * E | 0 | camp | 2019-12-01T09:00 | 2019-12-03T17:00
 * </pre>
 *
 * <p>Neither the file nor the folder above it has to exist: a missing file simply means
 * there is nothing saved yet, and saving creates whatever is missing. A line that is not
 * in the expected format is skipped rather than allowed to stop the app, and the number
 * of such lines is reported through {@link #getSkippedLineCount()}.
 */
public class Storage {

    private static final String SEPARATOR_PATTERN = "\\s*\\|\\s*";

    private final Path file;
    private int skippedLineCount;

    /**
     * Creates storage backed by the given file.
     *
     * @param file where tasks are saved. It does not have to exist yet.
     */
    public Storage(Path file) {
        this.file = file;
    }

    /**
     * Reads the saved tasks.
     *
     * <p>Blank lines are ignored. Lines that cannot be read are skipped and counted, so
     * a damaged file still gives back everything that is left of it.
     *
     * @return the tasks that could be read, in the order they were saved.
     * @throws EVException if the file itself cannot be read at all.
     */
    public ArrayList<Task> load() throws EVException {
        ArrayList<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;

        if (!Files.exists(file)) {
            return tasks;
        }
        if (Files.isDirectory(file)) {
            throw new EVException(file + " is a folder, not a file, so I cannot read your saved tasks."
                    + "\nI'm starting with an empty list.");
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new EVException("I could not read " + file + " (" + e.getMessage() + ")."
                    + "\nI'm starting with an empty list.");
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (EVException e) {
                skippedLineCount++;
            }
        }
        return tasks;
    }

    /**
     * Writes the given tasks, replacing whatever the file held before.
     *
     * @param tasks the tasks to save, in the order they should be numbered.
     * @throws EVException if the file or the folder above it cannot be written.
     */
    public void save(List<Task> tasks) throws EVException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileFormat());
        }
        try {
            Path folder = file.getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }
            Files.write(file, lines);
        } catch (IOException e) {
            throw new EVException("I could not save your tasks to " + file + " (" + e.getMessage() + ")."
                    + "\nThe list is still correct in this session, but the change may be lost after you exit.");
        }
    }

    /**
     * Returns how many lines the last {@link #load()} had to skip.
     *
     * @return the number of unreadable lines, counted afresh on every load.
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Returns the file being read and written.
     *
     * @return the save file, as given to the constructor.
     */
    public Path getFile() {
        return file;
    }

    /**
     * Rebuilds one task from its line in the save file.
     *
     * @param line one non-blank line of the file.
     * @return the task it stands for.
     * @throws EVException if the type, the done flag, the field count or a date is wrong.
     *     The message explains which, and is used for counting rather than shown to the user.
     */
    private static Task parseTask(String line) throws EVException {
        String[] fields = line.split(SEPARATOR_PATTERN);
        if (fields.length < 3) {
            throw new EVException("Too few fields: " + line);
        }
        for (String field : fields) {
            if (field.isBlank()) {
                throw new EVException("Blank field: " + line);
            }
        }

        Task task = switch (fields[0]) {
        case Todo.TYPE -> {
            requireFieldCount(fields, 3, line);
            yield new Todo(fields[2]);
        }
        case Deadline.TYPE -> {
            requireFieldCount(fields, 4, line);
            yield new Deadline(fields[2], DateTimes.fromFileFormat(fields[3]));
        }
        case Event.TYPE -> {
            requireFieldCount(fields, 5, line);
            yield new Event(fields[2],
                    DateTimes.fromFileFormat(fields[3]),
                    DateTimes.fromFileFormat(fields[4]));
        }
        default -> throw new EVException("Unknown task type: " + line);
        };

        if (fields[1].equals(Task.DONE_FLAG)) {
            task.markAsDone();
        } else if (!fields[1].equals(Task.NOT_DONE_FLAG)) {
            throw new EVException("Status is neither " + Task.DONE_FLAG + " nor "
                    + Task.NOT_DONE_FLAG + ": " + line);
        }
        return task;
    }

    /**
     * Checks that a line has exactly as many fields as its task type needs.
     *
     * @param fields the fields the line was split into.
     * @param expected how many fields this type of task has.
     * @param line the line itself, for the message.
     * @throws EVException if the count does not match.
     */
    private static void requireFieldCount(String[] fields, int expected, String line) throws EVException {
        if (fields.length != expected) {
            throw new EVException("Expected " + expected + " fields but found " + fields.length + ": " + line);
        }
    }
}
