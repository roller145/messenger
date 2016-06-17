package arhangel.dim.client.commands;

import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.ProtocolException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import javax.inject.Named;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by riv on 17.06.16.
 */
@Parameters(commandDescription = "send a message to the specified chat, chat must be in the list of user chats: /text <id> <message>")
@Named("/text")
public class CommandText extends Command {
    @Parameter(description = "chat_id")
    Long chatId = Long.valueOf(-1);

    @Parameter(description = "text")
    List<String> text;

    @Override
    public void execute() throws ExecutionException, IOException, ProtocolException {
        if (client.getUser().isLoginned()) {
            StringBuilder str = new StringBuilder();
            text.stream().forEach(str::append);
            logger.info("Sending message with text" + str.toString());
            TextMessage infoMessage = new TextMessage(chatId, client.getUser().getId(), str.toString(), LocalDateTime.now());
            client.send(infoMessage);
        } else {
            logger.error("You are not loginned");
        }
    }


}
