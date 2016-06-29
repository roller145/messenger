package arhangel.dim.core.commands;

import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.database.StorageException;

import java.io.IOException;
import java.util.List;

/**
 * Created by riv on 29.06.16.
 */
public class ChatCreateCommand extends GenericCommand {
    private Type type = Type.MSG_CHAT_CREATE;

    @Override
    Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException {
        ChatCreateMessage chatCreateMessage = (ChatCreateMessage) message;
        Long userId = chatCreateMessage.getSenderId();
        List<Long> users= chatCreateMessage.getUsers();
        Long chatId;
        if (!users.contains(userId)){
            users.add(0,userId);
        }
        try {
            chatId = session.getMessageStore().addChat(chatCreateMessage.getUsers());
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }
        AnswerMessage answerMessage = new AnswerMessage();
        answerMessage.setSenderId(userId);
        answerMessage.setId(message.getId());
        answerMessage.setText(
                String.format("Chat #%d with participants %s created\n",
                        chatId,
                        chatCreateMessage.getUsers().toString()
                ));

        return answerMessage;
    }
}
