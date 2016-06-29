package arhangel.dim.core.commands;

import arhangel.dim.core.messages.ChatHistoryMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.database.StorageException;

import java.io.IOException;
import java.util.List;

/**
 * Created by riv on 29.06.16.
 */
public class ChatHistoryCommand extends GenericCommand {
    Type type= Type.MSG_CHAT_HIST;
    @Override
    Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException {
        ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) message;

        List<TextMessage> messages;
        Long chatId = chatHistoryMessage.getChatId();
        try {
            messages = session.getMessageStore().getMessagesFromChat(chatId);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }

        ChatHistoryResultMessage resultMessage = new ChatHistoryResultMessage();
        resultMessage.setHistory(messages);
        resultMessage.setChatId(chatId);
        return resultMessage;
    }
}
