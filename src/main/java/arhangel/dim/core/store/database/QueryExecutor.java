package arhangel.dim.core.store.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

/**
 *
 */
public class QueryExecutor {
    private final String URL = "jdbc:postgresql://178.62.140.149:5432/UPML";
    private final String USERNAME = "trackuser";
    private final String PASSWORD = "trackuser";

    private Connection connection;


    QueryExecutor() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }


    // Простой запрос
    public <T> T execQuery(String query, ResultHandler<T> handler) throws SQLException, StorageException {
        Statement stmt = connection.createStatement();
        stmt.execute(query);
        ResultSet result = stmt.getResultSet();
        T value = null;
        try {
            value = handler.handle(result);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        result.close();
        stmt.close();

        return value;
    }

    // Подготовленный запрос
    public <T> T execQuery(String query, Map<Integer, Object> args, ResultHandler<T> handler) throws SQLException, StorageException {
        PreparedStatement stmt = connection.prepareStatement(query);
        for (Map.Entry<Integer, Object> entry : args.entrySet()) {
            stmt.setObject(entry.getKey(), entry.getValue());
        }
        ResultSet rs = stmt.executeQuery();
        T value = null;
        try {
            value = handler.handle(rs);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        rs.close();
        stmt.close();
        return value;
    }

    public <T> long execUpdate(String query, Map<Integer, Object> args) throws SQLException {
        long result = 0;
        try {
            PreparedStatement stmt = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);
            for (Map.Entry<Integer, Object> entry : args.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            result = rs.getLong(1);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return result;
    }


}
