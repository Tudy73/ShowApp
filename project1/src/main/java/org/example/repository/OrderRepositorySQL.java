package org.example.repository;

import org.example.model.security.User;
import org.example.model.validation.Notification;

import java.sql.*;

import static org.example.database.Constants.ERRORS.SOMETHING_IS_WRONG_WITH_THE_DATABASE;

public class OrderRepositorySQL implements OrderRepository{

    private final Connection connection;

    public OrderRepositorySQL(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void deleteForUser(Long id) {
        try {
            String sql = "DELETE FROM `order` WHERE `user_id` = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id.intValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean findOrder(Long id) {
        try {
            String sql =
                    "Select * from `order` where `user_id` = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id.intValue());
            ResultSet userResultSet = statement.executeQuery();
            if(userResultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    @Override
    public void addOrder(Long id) throws SQLException {
        try {
            PreparedStatement insertUserStatement = connection
                    .prepareStatement("INSERT INTO `order` values (null, ?)", Statement.RETURN_GENERATED_KEYS);
            insertUserStatement.setInt(1, id.intValue());
            insertUserStatement.executeUpdate();
           } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE from `order` where id >= 0";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
