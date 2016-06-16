package arhangel.dim.core.store.database;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.lections.objects.LoaderDemo;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by riv on 01.06.16.
 */
public class AskDBAboutMessages {

    public AskDBAboutMessages() {
    }

    public List<Long> getChatsByUserId(Long userId) throws SQLException, StorageException, ClassNotFoundException {
        QueryExecutor exec = new QueryExecutor();
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, userId);
        List<Long> result = exec.execQuery("SELECT chat_id FROM users_chats WHERE user_id = ?;", prepared, resultSet -> {
            List<Long> resultList = new LinkedList<>();
            try {
                while (resultSet.next()) {
                    Long chatId = resultSet.getLong("chat_id");
                    resultList.add(chatId);
                }
                return resultList;
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
        });
        return result;
    }

    public List<Long> getMessagesFromChat(Long chatId) throws SQLException, StorageException, ClassNotFoundException {
        QueryExecutor exec = new QueryExecutor();
        String getMessagesFromChatStatement =
                "SELECT text_id FROM chat_messages where chat_id = ?";
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);
        List<Long> result = exec.execQuery(getMessagesFromChatStatement, prepared, resultSet -> {
            List<Long> resultList = new LinkedList<>();
            try {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("text_id");
                    resultList.add(id);
                }
                return resultList;
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
        });
        return result;
    }

    public TextMessage addMessage(Long chatId, TextMessage message) throws SQLException, ClassNotFoundException {
        String setTextOfMessage =
                "INSERT INTO messages_text(text, text_date, sender_id) VALUES(?, ?, ?) RETURNING text_id";
        Map<Integer, Object> prepared1 = new HashMap<>();
        prepared1.put(1, message.getText());
        Timestamp dateTime = Timestamp.valueOf(message.getDateTime());
        prepared1.put(2, dateTime);
        Long senderId = message.getSenderId();
        prepared1.put(3, senderId);
        QueryExecutor exec = new QueryExecutor();
        Long textId = exec.execUpdate(setTextOfMessage, prepared1);

        String setMessageChat =
                "INSERT INTO chat_messages(chat_id, message_id) VALUES(?, ?)";

        Map<Integer, Object> prepared2 = new HashMap<>();
        prepared2.put(1, chatId);
        prepared2.put(2, textId);
        QueryExecutor exec1 = new QueryExecutor();
        exec1.execUpdate(setMessageChat, prepared2);
        return new TextMessage(chatId, senderId, message.getText(), dateTime.toLocalDateTime());
    }

    public Message getMessageById(Long messageId) throws SQLException, StorageException, ClassNotFoundException {
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, messageId);
        String getMessagesByIdStatement =
                "SELECT * FROM chat_messages INNER JOIN messages_texts" +
                        " ON chat_messages.message_id = messages_text.text_id " +
                        " WHERE messages_text.text_id = ?;";
        QueryExecutor exec = new QueryExecutor();
        Message res = exec.execQuery(getMessagesByIdStatement, prepared, resultSet -> {
            LocalDateTime date = resultSet.getTimestamp("text_date").toLocalDateTime();
            String text = resultSet.getString("text");
            Long chatId = resultSet.getLong("chat_id");
            Long userId = resultSet.getLong("user_id");
            TextMessage message = new TextMessage(chatId, userId, text, date);
            return message;
        });
        return res;
    }

    public List<Long> getChatById(Long chatId) throws SQLException, ClassNotFoundException, StorageException {
        QueryExecutor exec = new QueryExecutor();
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);
        List<Long> result = exec.execQuery("SELECT user_id FROM users_chats WHERE chat_id = ?;", prepared, resultSet -> {
            List<Long> resultList = new LinkedList<>();
            try {
                while (resultSet.next()) {
                    Long userId = resultSet.getLong("user_id");
                    resultList.add(userId);
                }
                return resultList;
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
        });
        return result;
    }

    public void addUserToChat(Long userId, Long chatId) throws SQLException, ClassNotFoundException {
        final String addUserStatement = "INSERT INTO users_chats (chat_id, user_id) VALUES (?,?);";
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);
        prepared.put(2, userId);
        QueryExecutor exec = new QueryExecutor();
        exec.execUpdate(addUserStatement, prepared);
    }
}
