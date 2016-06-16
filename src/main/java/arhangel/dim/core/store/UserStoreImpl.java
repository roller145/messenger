package arhangel.dim.core.store;

import arhangel.dim.core.User;
import arhangel.dim.core.store.database.AskDBAboutUsers;
import arhangel.dim.core.store.database.StorageException;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserStoreImpl implements UserStore {

    Map<Long, User> users = null;
    Map<String, User> nameCash = null;
    Connection connection = null;
    AskDBAboutUsers askingDB = null;

    private final String updateStatement = "UPDATE users SET username = ? AND  password = ? WHERE id = ?";


    UserStoreImpl(Connection connection) throws StorageException {
        this.connection = connection;
        askingDB = new AskDBAboutUsers();
        users = new HashMap<>();
        nameCash = new HashMap<>();
        try {
            Pair<Map<Long, User>, Map<String, User>> cash = askingDB.getAllUsers();
            users = cash.getKey();
            nameCash = cash.getValue();
        } catch (SQLException ex) {
            throw new StorageException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public User addUser(User user) throws SQLException, StorageException {
        User res = null;
        try {
            Long id = askingDB.addUser(user.getName(), user.getPassword());
            res = new User(id, user.getName(), user.getPassword());
            users.put(id, res);
        } catch (SQLException ex) {
            throw new StorageException(ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    @Override
    public User getUserById(Long id, String pass) {
        return null;
    }


    /**
     * Получить пользователя по логину/паролю
     * return null if user not found
     */
    @Override
    public User getUser(String login, String pass) throws StorageException {
        User res = null;
        if (nameCash.containsKey(login) && (nameCash.get(login).getPassword().equals(pass))) {
            return nameCash.get(login);
        } else {
            try {
                res = askingDB.getUser(login, pass);
                users.put(res.getId(), res);
                nameCash.put(res.getName(), res);

            } catch (SQLException ex) {
                throw new StorageException(ex);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    @Override
    public User getUserById(Long id) throws StorageException {
        User res = null;
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            try {
                res = askingDB.getUser(id);
                users.put(res.getId(), res);
                nameCash.put(res.getName(), res);

            } catch (SQLException ex) {
                throw new StorageException(ex);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    /**
     * Получить пользователя по id, например запрос информации/профиля
     * return null if user not found
     */


    @Override
    public User updateUser(User user) {
        return null;
    }

}
