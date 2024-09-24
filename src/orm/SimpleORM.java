package orm;

import java.sql.*;
import db.DatabaseConnection;
import entity.Table;
import entity.User;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class SimpleORM<T> {
    private Class<T> entityClass;
    private String tableName;

    public SimpleORM(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.tableName = getTableName(); // Dynamically get table name
    }

    // Get the table name from the @Table annotation
    private String getTableName() {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            return tableAnnotation.name();
        }
        throw new RuntimeException("No @Table annotation present on class: " + entityClass.getSimpleName());
    }

    // Create (INSERT)
    public void save(T entity) {

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Construct the SQL query dynamically based on the fields of the entity
            Field[] fields = entityClass.getDeclaredFields();
            StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + tableName + " (");

            // Build the column part of the SQL query
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true); // Allow access to private fields
                queryBuilder.append(fields[i].getName());
                if (i < fields.length - 1) {
                    queryBuilder.append(", ");
                }
            }
            queryBuilder.append(") VALUES (");

            // Build the value placeholders (e.g., ?, ?, ?)
            for (int i = 0; i < fields.length; i++) {
                queryBuilder.append("?");
                if (i < fields.length - 1) {
                    queryBuilder.append(", ");
                }
            }
            queryBuilder.append(")");

            String query = queryBuilder.toString();
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the values dynamically using reflection
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object value = fields[i].get(entity); // Get the field's value from the entity
                statement.setObject(i + 1, value); // Set the value in the prepared statement
            }

            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // Read (SELECT)
    public T findById(int id) {
        T entity = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                if (entityClass == User.class) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setAge(resultSet.getInt("age"));
                    entity = entityClass.cast(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    // Update
    public void update(T entity) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE " + tableName + " SET name = ?, email = ?, age = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            if (entity instanceof User) {
                User user = (User) entity;
                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.setInt(3, user.getAge());
                statement.setInt(4, user.getId());
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    public void delete(int id) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM " + tableName + " WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
