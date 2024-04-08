package org.example.repository;

import java.sql.*;

import static org.example.database.Constants.ERRORS.*;

public class ConfigRepositorySQL implements ConfigRepository {
    private final Connection connection;

    public ConfigRepositorySQL(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void updateValue(String name, double value) {
        try {
            String sql = "INSERT INTO `config` (id,setting,value) \n " +
                    "values (null, ?,?)" +
                    "ON DUPLICATE KEY UPDATE \n" +
                    "    value = VALUES(value);";
            PreparedStatement insertUserStatement = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            insertUserStatement.setString(1, name);
            insertUserStatement.setDouble(2, value);
            insertUserStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(SOMETHING_IS_WRONG_WITH_THE_DATABASE);
        }
    }

    @Override
    public double findValue(String name) {
        try {
            String sql =
                    "Select * from `config` where `setting` = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet userResultSet = statement.executeQuery();
            if (userResultSet.next()) {
                return userResultSet.getDouble("value");
            } else throw new RuntimeException(VALUE_NOT_FOUND);
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(SOMETHING_IS_WRONG_WITH_THE_DATABASE);
        }
    }
}
