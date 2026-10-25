package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Спільна логіка Service-рівня: логування успішних операцій, відхилених даних і помилок БД. */
abstract class BaseService {
    @FunctionalInterface
    interface SqlAction<T> { T call() throws SQLException; }

    protected final Logger log = Logger.getLogger(getClass().getName());

    protected <T> T run(String operation, SqlAction<T> action) throws SQLException {
        try {
            T result = action.call();
            log.info(operation + ": виконано");
            return result;
        } catch (IllegalArgumentException e) {
            log.warning(operation + ": відхилено — " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            log.log(Level.SEVERE, operation + ": помилка бази даних", e);
            throw e;
        }
    }
}
