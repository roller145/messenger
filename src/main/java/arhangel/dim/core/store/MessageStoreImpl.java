package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.store.database.AskDBAboutMessages;
import arhangel.dim.core.store.database.StorageException;

import java.sql.SQLException;
import java.util.List;

public class MessageStoreImpl implements MessageStore {

    private AskDBAboutMessages askingDB;

    public MessageStoreImpl() {
        this.askingDB = new AskDBAboutMessages();
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) throws StorageException {
        try {
            List<Long> resultList = askingDB.getChatsByUserId(userId);
            return resultList;
        } catch (SQLException ex) {
            throw new StorageException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Chat getChatById(Long chatId) throws StorageException {
        List<Long> users;
        try {
            users = askingDB.getChatById(chatId);
        } catch (SQLException ex) {
            throw new StorageException(ex);
        } catch (StorageException ex) {
            throw new StorageException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
        return new Chat(chatId, users);

    }

    @Override
    public List<TextMessage> getMessagesFromChat(Long chatId) throws StorageException {
        try {
            List<TextMessage> resultList = askingDB.getMessagesFromChat(chatId);
            return resultList;
        } catch (SQLException ex) {
            throw new StorageException(ex);
        } catch (StorageException ex) {
            throw new StorageException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Message getMessageById(Long messageId) throws SQLException, StorageException {
        try {
            Message message = askingDB.getMessageById(messageId);
            return message;
        } catch (SQLException ex) {
            throw new StorageException(ex);
        } catch (StorageException ex) {
            throw new StorageException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    @Override
    public Long addMessage(Long chatId, TextMessage message) throws StorageException {
        Long mess;
        try {
            mess = askingDB.addMessage(chatId, message);
        } catch (SQLException ex) {
            throw new StorageException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
        return mess;
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) throws StorageException {
        try {
            askingDB.addUserToChat(userId, chatId);
        } catch (SQLException ex) {
            throw new StorageException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Long addChat(List<Long> users) throws StorageException {
        Long chatId = null;
        try {
            chatId = askingDB.createChat(users.get(0));
        } catch (SQLException e) {
            throw new StorageException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (int it = 0; it < users.size();++it){
            this.addUserToChat(users.get(it),chatId);
        }
        return chatId;
    }

}