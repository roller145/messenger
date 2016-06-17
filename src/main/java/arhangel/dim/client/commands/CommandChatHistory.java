package arhangel.dim.client.commands;

import arhangel.dim.client.Client;
import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.net.ProtocolException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import javax.inject.Named;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by riv on 17.06.16.
 */
@Parameters(commandDescription = "a list of messages from the specified chat: /chat_history <chat_id>")
@Named("chat_history")
public class CommandChatHistory extends Command {

    @Parameter(description = "chat_id")
    Long chatId = Long.valueOf(-1);

    @Override
    public void execute() throws ExecutionException, IOException, ProtocolException {
        if (client.getUser().isLoginned()) {
            logger.info("Executing info request about my chats:)");
            ChatHistoryMessage infoMessage = new ChatHistoryMessage(client.getUser().getId());
            client.send(infoMessage);
        } else {
            logger.error("You are not loginned");
        }
    }
}
