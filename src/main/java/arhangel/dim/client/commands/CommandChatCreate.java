package arhangel.dim.client.commands;


import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.net.ProtocolException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.inject.name.Named;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by riv on 17.06.16.
 */
@Parameters(commandDescription = "create new chat with users with specified id list: /chat_create  -id <id1> <id2> ...")
@Named("/chat_create")
public class CommandChatCreate extends Command {
    @Parameter(description = "list of users to de in the chat")
    private List<Long> userIds;

    @Override
    public void execute() throws ExecutionException, IOException, ProtocolException {
        if (client.getUser().isLoginned()) {
            if (!userIds.isEmpty()) {
                userIds.add(client.getUser().getId());
                logger.info("Creating new chat");
                ChatCreateMessage loginMessage = new ChatCreateMessage(userIds);
                client.send(loginMessage);
            } else {
                logger.error("You have nobody to talk to");
            }
        } else {
            logger.error("You are not loginned");
        }
    }

}
