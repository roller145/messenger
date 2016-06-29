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
        List<Long> result = exec.execQuery("SELECT chat_id FROM user_chats WHERE user_id = ?;", prepared, resultSet -> {
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

    public List<TextMessage> getMessagesFromChat(Long chatId) throws SQLException, StorageException, ClassNotFoundException {
        QueryExecutor exec = new QueryExecutor();
        String getMessagesFromChatStatement =
                "SELECT * FROM chat_messages where chat_id = ?;";
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);
        List<TextMessage> result = exec.execQuery(getMessagesFromChatStatement, prepared, resultSet -> {
            List<TextMessage> resultList = new LinkedList<>();
            try {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("message_id");
                    LocalDateTime date = resultSet.getTimestamp("text_date").toLocalDateTime();
                    String text = resultSet.getString("text");
                    Long userId = resultSet.getLong("user_id");
                    TextMessage message = new TextMessage(chatId, userId, text, date);
                    resultList.add(message);
                }
                return resultList;
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
        });
        return result;
    }

    public Long addMessage(Long chatId, TextMessage message) throws SQLException, ClassNotFoundException {
        String setTextOfMessage =
                "INSERT INTO chat_messages(text, text_date, user_id, chat_id) VALUES(?, ?, ?, ?);";
        Map<Integer, Object> prepared1 = new HashMap<>();
        prepared1.put(1, message.getText());
        Timestamp dateTime = Timestamp.valueOf(message.getDateTime());
        prepared1.put(2, dateTime);
        Long senderId = message.getSenderId();
        prepared1.put(3, senderId);
        prepared1.put(4, chatId);
        QueryExecutor exec = new QueryExecutor();
        Long textId = exec.execUpdate(setTextOfMessage, prepared1);

        return textId;
    }

    public Message getMessageById(Long messageId) throws SQLException, StorageException, ClassNotFoundException {
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, messageId);
        String getMessagesByIdStatement =
                "SELECT * FROM chat_messages WHERE message_id = ?;";
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
        List<Long> result = exec.execQuery("SELECT user_id FROM user_chats WHERE chat_id = ?;", prepared, resultSet -> {
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
        final String addUserStatement = "INSERT INTO user_chats (chat_id, user_id) VALUES (?,?);";
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);
        prepared.put(2, userId);
        QueryExecutor exec = new QueryExecutor();
        exec.execUpdate(addUserStatement, prepared);
    }


    public Long createChat(Long id) throws SQLException, ClassNotFoundException {
        final String addUserStatement = "INSERT INTO chats(creator) VALUES(?);";
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, id);
        QueryExecutor exec = new QueryExecutor();
        Long chatId = exec.execUpdate(addUserStatement, prepared);
        return chatId;
    }
}
