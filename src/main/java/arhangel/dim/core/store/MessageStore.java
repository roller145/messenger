package arhangel.dim.core.store;

import java.sql.SQLException;
import java.util.List;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.store.database.StorageException;

/**
 * Хранилище информации о сообщениях
 */
public interface MessageStore {

    /**
     * получаем список ид пользователей заданного чата
     */
    List<Long> getChatsByUserId(Long userId) throws StorageException;

    /**
     * получить информацию о чате
     */
    Chat getChatById(Long chatId) throws SQLException, StorageException, ClassNotFoundException;

    /**
     * Список сообщений из чата
     */
    List<Long> getMessagesFromChat(Long chatId) throws StorageException;

    /**
     * Получить информацию о сообщении
     */
    Message getMessageById(Long messageId) throws SQLException, StorageException;

    /**
     * Добавить сообщение в чат
     */
    Message addMessage(Long chatId, Message message) throws StorageException;

    /**
     * Добавить пользователя к чату
     */
    void addUserToChat(Long userId, Long chatId) throws StorageException;


}
