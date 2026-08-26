public class Event extends Task {

    public static final String TYPE = "E";

    protected String from;
    protected String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return encode(TYPE, from, to);
    }

    @Override
    public String toString() {
        return "[" + TYPE + "]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
