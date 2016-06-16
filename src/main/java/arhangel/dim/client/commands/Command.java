package arhangel.dim.client.commands;

import arhangel.dim.client.Client;
import arhangel.dim.core.net.ProtocolException;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by riv on 16.06.16.
 */
public abstract class Command {

    private static final String[] NO_ALIASES = new String[]{};

    protected Logger logger;
    private String commandName;
    protected Client client;

    protected Command(Client client) {
        this.client = client;
        logger = LoggerFactory.getLogger(getClass());
        commandName = getClass().getAnnotation(Named.class).value();
    }

    public String[] getAliases() {
        return NO_ALIASES;
    }

    public final String getCommandName() {
        return commandName;
    }

    public abstract void execute() throws ExecutionException, IOException, ProtocolException;
}
