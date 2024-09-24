package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class MySQLConnection implements DBConnectionInterface {
    private static MySQLConnection instance;
    private static Connection connection = null;
    private String provider;

    private MySQLConnection(String provider) {
        this.provider = provider;
    }

    public static MySQLConnection getInstance(String provider) {
        if (instance == null && "mySQL".equalsIgnoreCase(provider)) {
            instance = new MySQLConnection(provider);
        }
        return instance;
    }

    @Override
    public Connection getConnection() {
        if (connection == null) {
            connection = readPropertyFile();
        }
        return connection;
    }

    private Connection readPropertyFile() {
        if (!"mySQL".equalsIgnoreCase(provider)) {
            System.out.println("Unsupported provider: " + provider);
            return null;
        }

        try (InputStream input = DBConnectionInterface.class.getClassLoader().getResourceAsStream("mysql.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find mysql.properties");
                return null;
            }
            // Load the properties file
            prop.load(input);

            // Get the values from the properties file
            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.username");
            String password = prop.getProperty("db.password");
            String driver = prop.getProperty("db.driver");

            // Register the driver and establish the connection
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }
}
