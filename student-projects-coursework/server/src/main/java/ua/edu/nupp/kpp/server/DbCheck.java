package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Перевірка JDBC-з'єднання: mvn compile exec:java */
public class DbCheck {
    private static final Logger LOG = Logger.getLogger(DbCheck.class.getName());
    private static final String[] TABLES = {"student", "project", "submission", "review"};

    public static void main(String[] args) {
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement()) {
            System.out.println("З'єднання з MySQL встановлено: " + c.getMetaData().getURL());
            for (String table : TABLES) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
                    rs.next();
                    System.out.println("  " + table + ": " + rs.getInt(1) + " записів");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Не вдалося підключитися до бази даних", e);
        }
    }
}
