public class Deadline extends Task {

    public static final String TYPE = "D";

    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return encode(TYPE, by);
    }

    @Override
    public String toString() {
        return "[" + TYPE + "]" + super.toString() + " (by: " + by + ")";
    }
}
