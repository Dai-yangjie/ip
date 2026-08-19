public class Task {

    protected String description;
    protected boolean isDone;
    protected String typeIcon;
    protected String by;
    protected String from;
    protected String to;

    private Task(String description, String typeIcon) {
        this.description = description;
        this.isDone = false;
        this.typeIcon = typeIcon;
    }

    public static Task createTodo(String description) {
        return new Task(description, "T");
    }

    public static Task createDeadline(String description, String by) {
        Task task = new Task(description, "D");
        task.by = by;
        return task;
    }

    public static Task createEvent(String description, String from, String to) {
        Task task = new Task(description, "E");
        task.from = from;
        task.to = to;
        return task;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        String base = "[" + typeIcon + "][" + getStatusIcon() + "] " + description;
        if (typeIcon.equals("D")) {
            return base + " (by: " + by + ")";
        } else if (typeIcon.equals("E")) {
            return base + " (from: " + from + " to: " + to + ")";
        } else {
            return base;
        }
    }
}
