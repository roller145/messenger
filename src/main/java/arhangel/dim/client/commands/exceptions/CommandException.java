package arhangel.dim.client.commands.exceptions;

/**
 * Created by riv on 16.06.16.
 */
public class CommandException extends Exception {
    private String message;

    CommandException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
