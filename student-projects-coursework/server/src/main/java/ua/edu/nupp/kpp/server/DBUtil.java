package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Створює JDBC-з'єднання з MySQL. Значення за замовчуванням підходять для XAMPP. */
public class DBUtil {
    private static final String URL = System.getProperty("db.url",
            "jdbc:mysql://localhost:3306/student_projects?useUnicode=true&characterEncoding=UTF-8");
    private static final String USER = System.getProperty("db.user", "root");
    private static final String PASSWORD = System.getProperty("db.password", "");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
