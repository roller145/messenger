package arhangel.dim.core.commands;

import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.database.StorageException;

import java.io.IOException;
import java.util.List;

/**
 * Created by riv on 29.06.16.
 */
public class ChatListCommand extends GenericCommand {
    private Type type = Type.MSG_CHAT_LIST;

    @Override
    Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException {
        ChatListMessage chatListMessage = (ChatListMessage) message;
        Long userId = chatListMessage.getSenderId();
        List<Long> chats;
        try {
            chats = session.getMessageStore().getChatsByUserId(userId);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        ChatListResultMessage answer = new ChatListResultMessage();
        answer.setChats(chats);
        return answer;
    }
}
