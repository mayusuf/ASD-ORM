package db;

import java.sql.ResultSet;
import java.sql.Statement;
public class TestConnection {
    public static void main(String[] args) {
        try {
            DBConnectionInterface instance =  MySQLConnection.getInstance("mySQL");
            Statement stmt = instance.getConnection().createStatement();
            String sql = "SELECT * FROM customer";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Customer ID: " + rs.getInt("id"));
                System.out.println("Customer Name: " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}