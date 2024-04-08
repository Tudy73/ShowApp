package org.example.repository.security;

import org.example.model.product.Report;
import org.example.model.security.ERole;
import org.example.model.security.User;
import org.example.model.validation.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.database.Constants.ERRORS.*;
import static org.example.database.Constants.TABLES.USER;

public class UserRepositorySQL implements UserRepository {

    private final Connection connection;
    private final RoleRepository roleRepository;

    public UserRepositorySQL(Connection connection, RoleRepository roleRepository) {
        this.connection = connection;
        this.roleRepository = roleRepository;
    }

    @Override
    public Notification<User> findByUsernameAndPassword(String username, String password) {
        Notification<User> resultNotification = new Notification<>();
        try {
            Statement statement = connection.createStatement();

            String fetchUserSql =
                    "Select * from `" + USER + "` where `username`=\'" + username + "\' and `password`=\'" + password + "\'";
            ResultSet userResultSet = statement.executeQuery(fetchUserSql);
            resultNotification = findUser(userResultSet);
        } catch (SQLException e) {
            System.out.println(e);
            resultNotification.addError(SOMETHING_IS_WRONG_WITH_THE_DATABASE);
        }
        return resultNotification;
    }

    @Override
    public User create(User user) throws SQLException {
        try {
            PreparedStatement insertUserStatement = connection
                    .prepareStatement("INSERT INTO user values (null, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            insertUserStatement.setString(1, user.getUsername());
            insertUserStatement.setString(2, user.getPassword());
            insertUserStatement.setInt(3, user.getPoints());
            insertUserStatement.setDouble(4, user.getMoney());
            insertUserStatement.executeUpdate();

            ResultSet rs = insertUserStatement.getGeneratedKeys();
            rs.next();
            long userId = rs.getLong(1);
            user.setId(userId);

            roleRepository.setRoleOfUser(user, user.getRole());

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE from user where id >= 0";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long user_id) {
        try {
            String sql = "DELETE FROM user WHERE `id` = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, user_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String fetchUserSql = "Select * from user";
            ResultSet userResultSet = statement.executeQuery(fetchUserSql);
            result.addAll(findUsers(userResultSet));
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    @Override
    public void update(User user) throws SQLException {
        try {
            String sql = "UPDATE `user` \n" +
                    "SET `username` = ?, `points` = ?, `money` = ?\n" +
                    "WHERE `id` = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setInt(2, user.getPoints());
            statement.setDouble(3, user.getMoney());
            statement.setInt(4, user.getId().intValue());
            statement.executeUpdate();
            roleRepository.setRoleOfUser(user, user.getRole());

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Notification<User> findById(Long id) {
        Notification<User> resultNotification = new Notification<>();
        try {
            String sql =
                    "Select * from user where `id` = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id.intValue());
            ResultSet userResultSet = statement.executeQuery();
            resultNotification = findUser(userResultSet);

        } catch (SQLException e) {
            System.out.println(e);
            resultNotification.addError(SOMETHING_IS_WRONG_WITH_THE_DATABASE);
        }
        return resultNotification;
    }

    @Override
    public List<User> findByRole(ERole role) {
        String sql =
                "SELECT u.* FROM `user` u " +
                        "INNER JOIN `user_role` ur ON u.`id` = ur.`user_id` " +
                        "INNER JOIN `role` r ON ur.`role_id` = r.`id` " +
                        "WHERE r.`role` = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, role.toString());
            ResultSet userResultSet = statement.executeQuery();
            return findUsers(userResultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Notification<Report> findReport(long user_id) {
        Notification<Report> resultNotification = new Notification<>();
        try {
            String fetchUserSql =
                    "Select * from `report` where `user_id` = ?";
            PreparedStatement statement = connection.prepareStatement(fetchUserSql);
            statement.setInt(1, (int) user_id);
            ResultSet userResultSet = statement.executeQuery();
            if (userResultSet.next()) {
                Report report = Report.builder()
                        .soldItems(userResultSet.getLong("items_sold"))
                        .totalMoney(userResultSet.getDouble("total_money"))
                        .noCustomers(userResultSet.getLong("no_customers"))
                        .build();
                resultNotification.setResult(report);
            } else {
                resultNotification.addError(NO_REPORT_FOUND);
            }
        } catch (SQLException e) {
            System.out.println(e);
            resultNotification.addError(SOMETHING_IS_WRONG_WITH_THE_DATABASE);
        }
        return resultNotification;
    }

    @Override
    public void updateReport(long user_id, Report report) {
        try {
            String fetchUserSql =
                    "INSERT INTO report (id, user_id, items_sold, total_money, no_customers)\n" +
                            "VALUES (NULL, ?, ?, ?, ?)\n" +
                            "ON DUPLICATE KEY UPDATE \n" +
                            "    items_sold = items_sold + VALUES(items_sold),\n" +
                            "    total_money = total_money + VALUES(total_money),\n" +
                            "    no_customers = no_customers + VALUES(no_customers);";
            PreparedStatement statement = connection.prepareStatement(fetchUserSql);
            statement.setInt(1, (int) user_id);
            statement.setInt(2, report.getSoldItems().intValue());
            statement.setDouble(3, report.getTotalMoney());
            statement.setInt(4, report.getNoCustomers().intValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private Notification<User> findUser(ResultSet userResultSet) {
        Notification<User> resultNotification = new Notification<>();
        try {
            if (userResultSet.next()) {
                User user = User.builder()
                        .username(userResultSet.getString("username"))
                        .password(userResultSet.getString("password"))
                        .role(roleRepository.findRoleForUser(userResultSet.getLong("id")))
                        .points(userResultSet.getInt("points"))
                        .money(userResultSet.getDouble("money"))
                        .id(userResultSet.getLong("id"))
                        .build();
                resultNotification.setResult(user);
                return resultNotification;
            } else {
                resultNotification.addError(NO_USER_FOUND);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultNotification;
    }

    private List<User> findUsers(ResultSet userResultSet) {
        List<User> result = new ArrayList<>();
        try {
            while (userResultSet.next()) {
                User user = User.builder()
                        .username(userResultSet.getString("username"))
                        .password(userResultSet.getString("password"))
                        .role(roleRepository.findRoleForUser(userResultSet.getLong("id")))
                        .points(userResultSet.getInt("points"))
                        .money(userResultSet.getDouble("money"))
                        .id(userResultSet.getLong("id"))
                        .build();
                result.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }
}
