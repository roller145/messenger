package arhangel.dim.core.store.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Обобщенный интерфейс обработки результата
 */

public interface ResultHandler<T> {
    T handle(ResultSet resultSet) throws SQLException, StorageException;
}