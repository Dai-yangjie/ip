package ev.parser;

import ev.EVException;

public enum CommandWord {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    ON("on"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye");

    private final String keyword;

    CommandWord(String keyword) {
        this.keyword = keyword;
    }

    public static CommandWord fromKeyword(String keyword) throws EVException {
        for (CommandWord command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }
        throw new EVException("I don't know what \"" + keyword + "\" means.\n"
                + "I understand: " + listKeywords() + ".");
    }

    public static String listKeywords() {
        StringBuilder keywords = new StringBuilder();
        for (CommandWord command : values()) {
            if (keywords.length() > 0) {
                keywords.append(", ");
            }
            keywords.append(command.keyword);
        }
        return keywords.toString();
    }
}
