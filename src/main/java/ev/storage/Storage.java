package ev.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ev.DateTimes;
import ev.EvException;
import ev.task.Deadline;
import ev.task.Event;
import ev.task.Task;
import ev.task.Todo;

public class Storage {

    private static final String SEPARATOR_PATTERN = "\\s*\\|\\s*";

    private final Path file;
    private int skippedLineCount = 0;

    public Storage(Path file) {
        this.file = file;
    }

    public ArrayList<Task> load() throws EvException {
        ArrayList<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;

        if (!Files.exists(file)) {
            return tasks;
        }
        if (Files.isDirectory(file)) {
            throw new EvException(file + " is a folder, not a file, so I cannot read your saved tasks."
                    + "\nI'm starting with an empty list.");
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new EvException("I could not read " + file + " (" + e.getMessage() + ")."
                    + "\nI'm starting with an empty list.");
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (EvException e) {
                skippedLineCount++;
            }
        }
        return tasks;
    }

    public void save(List<Task> tasks) throws EvException {
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
            throw new EvException("I could not save your tasks to " + file
                    + " (" + e.getMessage() + ")."
                    + "\nThe list is still correct in this session,"
                    + " but the change may be lost after you exit.");
        }
    }

    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    public Path getFile() {
        return file;
    }

    private static Task parseTask(String line) throws EvException {
        String[] fields = line.split(SEPARATOR_PATTERN);
        if (fields.length < 3) {
            throw new EvException("Too few fields: " + line);
        }
        for (String field : fields) {
            if (field.isBlank()) {
                throw new EvException("Blank field: " + line);
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
        default -> throw new EvException("Unknown task type: " + line);
        };

        if (fields[1].equals(Task.DONE_FLAG)) {
            task.markAsDone();
        } else if (!fields[1].equals(Task.NOT_DONE_FLAG)) {
            throw new EvException("Status is neither " + Task.DONE_FLAG + " nor "
                    + Task.NOT_DONE_FLAG + ": " + line);
        }
        return task;
    }

    private static void requireFieldCount(String[] fields, int expected, String line)
            throws EvException {
        if (fields.length != expected) {
            throw new EvException("Expected " + expected + " fields but found "
                    + fields.length + ": " + line);
        }
    }
}
