package db;

import java.sql.Connection;

public interface DBConnectionInterface {
    Connection getConnection();
}