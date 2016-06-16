package arhangel.dim.core.store;

import arhangel.dim.core.User;
import arhangel.dim.core.store.database.StorageException;

import java.sql.SQLException;

/**
 * Хранилище информации о пользователе
 */
public interface UserStore {

    /**
     * Добавить пользователя в хранилище
     * Вернуть его же
     */
    User addUser(User user) throws SQLException, StorageException;

    User getUserById(Long id, String pass);

    /**
     * Обновить информацию о пользователе
     */
    User updateUser(User user);

    /**
     *
     * Получить пользователя по логину/паролю
     * return null if user not found
     */
    User getUser(String login, String pass) throws StorageException;

    /**
     *
     * Получить пользователя по id, например запрос информации/профиля
     * return null if user not found
     */
    User getUserById(Long id) throws StorageException;
}
