package arhangel.dim.client.commands;

import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.net.ProtocolException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.inject.Inject;
import com.google.inject.name.Named;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Parameters(commandDescription = "get info about user: /info -id <id>")
@Named("/info")
class CommandInfo extends Command {
    @Parameter(description = "user id")
    private Long id = Long.valueOf(-1);

    private boolean isMe() {
        return (id.equals(Long.valueOf(-1)));
    }

    @Override
    public void execute() throws ExecutionException, IOException, ProtocolException {
        if (isMe()) {
            logger.info("Executing info request about me:)");
            InfoMessage infoMessage = new InfoMessage(client.getClient());
            client.send(infoMessage);
        } else {
            logger.info("Executing info request with parameter: [id=%d]", id);
            InfoMessage infoMessage = new InfoMessage(id);
            client.send(infoMessage);
        }

    }
}


