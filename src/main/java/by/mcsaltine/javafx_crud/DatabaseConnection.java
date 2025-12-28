package by.mcsaltine.javafx_crud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/javafx_crud?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";  // Измени на свой
    private static final String PASSWORD = "";  // Измени на свой

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
