package arhangel.dim.core.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.AnswerMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.database.StorageException;
import arhangel.dim.server.Server;

import java.io.IOException;
import java.sql.SQLException;


public class TextCommand extends GenericCommand {
    private final Server server;
    private Type type = Type.MSG_TEXT;

    public TextCommand(Server server) {
        super();
        this.server = server;
    }

    @Override
    Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException {
        TextMessage textMessage = (TextMessage) message;
        Long chatId = textMessage.getChatId();
        Long messageId;
        {
            try {
                messageId = session.getMessageStore().addMessage(chatId, textMessage);
            } catch (StorageException e) {
                throw new CommandException("Database failed", e);
            }
        }
        textMessage.setId(messageId);
        Chat chat = null;
        try {
            chat = session.getMessageStore().getChatById(textMessage.getChatId());
            if (chat == null) {
                AnswerMessage answer = new AnswerMessage();
                answer.setId(messageId);
                answer.setText("No such chat");
                return answer;
            }
        } catch (StorageException|SQLException e) {
            throw new CommandException("Database failed", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        AnswerMessage response = new AnswerMessage();
        response.setText(session.getUser() + " wrote to chat " + chat.getId() + " : " +
                textMessage.getText());
        response.setSenderId(textMessage.getSenderId());

        for (Long chatUsersId : chat.getUsers()) {
            for (Session s : server.getSessions()) {
                if (s.getUser() != null && chatUsersId.equals(s.getUser().getId())) {
                    s.send(response);
                }
            }
        }

        AnswerMessage answer = new AnswerMessage();
        answer.setId(messageId);
        answer.setText("Message sent");
        return answer;
    }

}
