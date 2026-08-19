public enum Command {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    public static Command fromKeyword(String keyword) throws EVException {
        for (Command command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }
        throw new EVException("I don't know what \"" + keyword + "\" means.\n"
                + "I understand: " + listKeywords() + ".");
    }

    public static String listKeywords() {
        StringBuilder keywords = new StringBuilder();
        for (Command command : values()) {
            if (keywords.length() > 0) {
                keywords.append(", ");
            }
            keywords.append(command.keyword);
        }
        return keywords.toString();
    }
}
