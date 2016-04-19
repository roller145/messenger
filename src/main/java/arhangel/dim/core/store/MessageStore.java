package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import java.lang.String;
import com.sun.xml.internal.ws.api.message.MessageWritable;

/**
 * Хранилище информации о сообщениях
 */
public class MessageStore {

    Connection connection = null;
    private final String url = "jdbc:postgresql://178.62.140.149:5432/roller145";
    private final String name = "trackuser";
    private final String password = "trackuser";
    private final String getMessagesFromChatStatement =
            "SELECT * FROM chat_messages INNER JOIN messages_texts " +
            "ON chat_messages.message_id = messages_text.text_id " +
            "WHERE chat_messages.chat_id = ?;";
    private final String getChatsByUserIdStatement =
            "SELECT * FROM users_chats WHERE user_id = ?;";
    private final String getMessagesByIdStatement =
            "SELECT * FROM chat_messages INNER JOIN messages_texts" +
            " ON chat_messages.message_id = messages_text.text_id " +
            " WHERE messages_text.text_id = ?;";
    private final String setTextOfMessage =
            "INSERT INTO messages_text(text, text_date) VALUES(?, ?) RETURNING text_id";
    private final String setMessageChat =
            "INSERT INTO chat_messages(chat_id, message_id) VALUES(?, ?)";
    private final String setUserToChat =
            "INSERT INTO users_chats(chat_id, message_id) VALUES(?, ?)";

    /**
     * получаем список ид пользователей заданного чата
     */
    List<Chat> getChatsByUserId(Long userId) {
        List<Chat> resultList = new LinkedList<>();
        PreparedStatement statement = null;
        PreparedStatement statementChats = null;
        try {
            statement = connection.prepareStatement(getChatsByUserIdStatement);
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Chat chat = new Chat(result.getLong("chat_id"));
                resultList.add(chat);
            }
            return resultList;
        } catch (SQLException ex) {
            ex.getMessage();
            return null;
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Список сообщений из чата
     */
    List<TextMessage> getMessagesFromChat(Long chatId) {
        List<TextMessage> resultList = new LinkedList<>();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(getMessagesFromChatStatement);
            statement.setLong(1, chatId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                TextMessage message = new TextMessage();
                message.setDate(result.getDate("text_date"));
                message.setText(result.getString("text"));
                message.setChatId(chatId);

                resultList.add(message);
            }
            return resultList;
        } catch (SQLException ex) {
            ex.getMessage();
            return null;
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Получить информацию о сообщении
     */
    Message getMessageById(Long messageId) {
        TextMessage message = new TextMessage();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(getMessagesByIdStatement);
            statement.setLong(1, messageId);
            ResultSet result = statement.executeQuery();
            message.setDate(result.getDate("text_date"));
            message.setText(result.getString("text"));
            message.setChatId(result.getLong("message_id"));

            return message;
        } catch (SQLException ex) {
            ex.getMessage();
            return null;
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Добавить сообщение в чат
     */
    void addMessage(Long chatId, TextMessage message) {
        PreparedStatement statement = null;
        Long messageId;
        try {
            statement = connection.prepareStatement(setTextOfMessage);
            statement.setString(1, message.getText());
            statement.setDate(2, new java.sql.Date(message.getDate().getTime()));
            ResultSet result = statement.executeQuery();
            messageId = result.getLong("text_id");
            statement = connection.prepareStatement(setMessageChat);
            statement.setLong(1, chatId);
            statement.setLong(2, messageId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.getMessage();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Добавить пользователя к чату
     */
    void addUserToChat(Long userId, Long chatId) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(setUserToChat);
            statement.setLong(2, userId);
            statement.setLong(1, chatId);
            ResultSet result = statement.executeQuery();
        } catch (SQLException ex) {
            ex.getMessage();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}
