package db;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/asd";
    private static final String USER = "springstudent";
    private static final String PASSWORD = "springstudent";

    private static Connection connection;

    private DatabaseConnection() {
    }

    public static Connection getConnection () throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

}

