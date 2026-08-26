package ev.task;

public class Todo extends Task {

    public static final String TYPE = "T";

    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return encode(TYPE);
    }

    @Override
    public String toString() {
        return "[" + TYPE + "]" + super.toString();
    }
}
