package ev;

/**
 * Something EV cannot do, described in a message meant to be shown to the user.
 *
 * <p>Every message is written so that it can be printed as it is: it says what went
 * wrong and, where useful, shows an example of what to type instead.
 */
public class EVException extends Exception {

    /**
     * Creates an exception carrying a message for the user.
     *
     * @param message text to show the user, which may span several lines.
     */
    public EVException(String message) {
        super(message);
    }
}
