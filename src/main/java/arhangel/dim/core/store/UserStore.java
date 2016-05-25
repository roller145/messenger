package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Хранилище информации о пользователе
 */
public class UserStore {
    Map<Long, User> users = null;
    Connection connection = null;
    //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
    private final String url = "jdbc:postgresql://178.62.140.149:5432/roller145";
    private final String name = "trackuser";
    private final String password = "trackuser";
    private final String updateStatement = "UPDATE users SET username = ? AND  password = ? WHERE id = ?";
    private final String selectAllStatement = "SELECT * FROM users";
    private final String insertStatement = "INSERT INTO users(username, password) values(?, ?) RETURNING id";
    private final String selectUserStatement = "SELECT id FROM users WHERE username = ? AND  password = ?";


    UserStore() throws ClassNotFoundException {
        users = new HashMap<>();
        Class.forName("org.postgresql.Driver");
        try {
            connection = DriverManager.getConnection(url, name, password);
            PreparedStatement statement = null;
            statement = connection.prepareStatement(selectAllStatement);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Long id = result.getLong("id");
                String username = result.getString("username");
                String password = result.getString("password");
                System.out.println(id.toString() + "\t - " + username);
                users.put(id, new User(id, username, password));
            }
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


    User addUser(User user) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(insertStatement);
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            ResultSet result = statement.executeQuery();
            user.setId(result.getLong("id"));
            users.put(user.getId(), user);
            return user;
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
     * Получить пользователя по логину/паролю
     * return null if user not found
     */
    User getUser(String login, String pass) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(selectUserStatement);
            statement.setString(1, login);
            statement.setString(2, pass);
            ResultSet result = statement.executeQuery();
            if (result.getFetchSize() == 0) {
                return null;
            } else {
                return users.get(result.getLong("id"));
            }
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
     * Получить пользователя по id, например запрос информации/профиля
     * return null if user not found
     */
    User getUserById(Long id, String pass) {
        User user = users.get(id);
        if (!users.containsKey(id)) {
            return null;
        } else {
            return user;
        }
    }

    public User updateUser(User user) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(updateStatement);
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setLong(3, user.getId());
            ResultSet result = statement.executeQuery();
            users.remove(user.getId());
            users.put(user.getId(), user);
            return user;
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

}
