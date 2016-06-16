package arhangel.dim.core.store.database;

import arhangel.dim.core.User;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import java.util.Map;

/**
 * Created by riv on 31.05.16.
 */
public class AskDBAboutUsers {

    public AskDBAboutUsers() {
    }

    public Pair<Map<Long, User>, Map<String, User>> getAllUsers() throws SQLException, StorageException, ClassNotFoundException {
        QueryExecutor exec = new QueryExecutor();
        Pair<Map<Long, User>, Map<String, User>> cash = exec.execQuery("SELECT * FROM users;", (rset) -> {
            Map<Long, User> data = new HashMap<>();
            Map<String, User> nameCash = new HashMap<>();
            while (rset.next()) {
                Long id = rset.getLong("id");
                String username = rset.getString("username");
                String password = rset.getString("password");
                User user = new User(id, username, password);
                nameCash.put(username, user);
                data.put(id, user);
            }
            return new Pair<>(data, nameCash);
        });
        return cash;
    }

    public Long addUser(String username, String password) throws SQLException, StorageException, ClassNotFoundException {
        QueryExecutor exec = new QueryExecutor();
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, username);
        prepared.put(2, password);
        Long id = exec.execUpdate("INSERT INTO users(username, password) values(?, ?) RETURNING id;", prepared);
        return id;
    }


    public User getUser(String login, String pass) throws SQLException, StorageException, ClassNotFoundException {
        QueryExecutor executor = new QueryExecutor();
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, login);
        prepared.put(2, pass);
        User res = executor.execQuery("SELECT id FROM users WHERE username = ? AND password = ?;", prepared, (resultSet) -> {
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                return new User(id, login, pass);
            } else {
                throw new SQLException("No such user");
            }
        });
        return res;
    }

    public User getUser(Long id) throws SQLException, StorageException, ClassNotFoundException {
        QueryExecutor executor = new QueryExecutor();
        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, id);
        User res = executor.execQuery("SELECT id FROM users WHERE id = ? ;", prepared, (resultSet) -> {
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                User user = new User(id, username, password);
                return user;
            } else {
                throw new SQLException("No such user");
            }
        });
        return res;
    }
}
