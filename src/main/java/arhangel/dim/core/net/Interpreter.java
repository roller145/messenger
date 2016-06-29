package arhangel.dim.core.net;

import arhangel.dim.core.commands.CommandException;
import arhangel.dim.core.commands.GenericCommand;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;

import java.io.IOException;
import java.util.Map;

/**
 * Created by riv on 26.06.16.
 */
public class Interpreter {

    private final Map<Type, GenericCommand> commands;

    public Interpreter(Map<Type, GenericCommand> commands) {
        this.commands = commands;
    }

    public void handleMessage(Message message, Session session) throws CommandException, IOException, ProtocolException {
        Type messageType = message.getType();
        if (messageType == null) {
            throw new CommandException("Message type is null");
        } else if (commands.containsKey(messageType)) {
            commands.get(messageType).execute(session, message);
        } else {
            throw new CommandException("Undefined message type");
        }
    }
}