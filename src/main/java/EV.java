import java.util.Scanner;

public class EV {

    private static final String LINE = "____________________________________________________________";

    private static final String COMMAND_BYE = "bye";

    private static final String BANNER = " _______     __\n"
            + "|   ____|   /  \\\n"
            + "|  |__     |    |\n"
            + "|   __|    |    |\n"
            + "|  |____    \\  /\n"
            + "|_______|    \\/\n";

    public static void main(String[] args) {
        System.out.println(BANNER);
        reply("Hello! I'm EV.\nWhat can I do for you?");

        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String command = in.nextLine().trim();
            if (command.equals(COMMAND_BYE)) {
                break;
            }
            reply(command);
        }

        reply("Bye. Hope to see you again soon!");
    }

    private static void reply(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
