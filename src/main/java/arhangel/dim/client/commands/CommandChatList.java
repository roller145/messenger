package arhangel.dim.client.commands;

/**
 * Created by riv on 17.06.16.
 */


import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.net.ProtocolException;
import com.beust.jcommander.Parameters;
import com.google.inject.Inject;
import com.google.inject.name.Named;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Parameters(commandNames = {"/chat_list"}, commandDescription = "get list of chats for you: /chat_list")
@Named("/chat_list")
public class CommandChatList extends Command {
    @Override
    public void execute() throws ExecutionException, IOException, ProtocolException {
        if (client.getUser().isLoginned()) {
            logger.info("Executing info request about my chats:)");
            ChatListMessage infoMessage = new ChatListMessage(client.getUser().getId());
            client.send(infoMessage);
        } else {
            logger.error("You are not loginned");
        }

    }
}

