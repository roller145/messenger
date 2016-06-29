package arhangel.dim.core.commands;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by riv on 26.06.16.
 */
public abstract class GenericCommand implements Command{
    public static Logger log  = LoggerFactory.getLogger(GenericCommand.class);
    private Type type;
    abstract Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException;
    
    @Override
    public void execute(Session session, Message message) throws ProtocolException, IOException, CommandException {
        log.info("Handling message {} with {}", message, getCommandName());
        Message answer = handleMessage(session, message);
        log.info("Sending answer: {}", answer);
        try {
            session.send(answer);
        } catch (ProtocolException | IOException e) {
            throw new CommandException("Could not send answer", e);
        }
    }

    public String getCommandName() {
        return this.getClass().toString();
    }
}
