package org.example.repository.security;

import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.example.database.Constants.TABLES.ROLE;
import static org.example.database.Constants.TABLES.USER_ROLE;

public class RoleRepositorySQL implements RoleRepository {
  private final Connection connection;

  public RoleRepositorySQL(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void create(ERole role) {
    try {
      PreparedStatement insertStatement = connection
          .prepareStatement("INSERT IGNORE INTO " + ROLE + " values (null, ?)");
      insertStatement.setString(1, role.toString());
      insertStatement.executeUpdate();
    } catch (SQLException e) {

    }
  }

  @Override
  public Role findRoleByTitle(ERole role) {
    Statement statement;
    try {
      statement = connection.createStatement();
      String fetchRoleSql = "Select * from " + ROLE + " where `role`=\'" + role.toString() + "\'";
      ResultSet roleResultSet = statement.executeQuery(fetchRoleSql);
      if(roleResultSet.next()) {
        Long roleId = roleResultSet.getLong("id");
        String roleTitle = roleResultSet.getString("role");
        return new Role(roleId, roleTitle);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Role findRoleForUser(long userId) {
    Statement statement;
    try {
      statement = connection.createStatement();
      String fetchRoleSql = "Select * from " + USER_ROLE + " where `user_id`=" + userId;
      ResultSet roleResultSet = statement.executeQuery(fetchRoleSql);
      if(roleResultSet.next()) {
        Long roleId = roleResultSet.getLong("role_id");
        String nextSql = "Select * from " + ROLE + " where `id`=" + roleId;
        roleResultSet = statement.executeQuery(nextSql);
        roleResultSet.next();
        String roleTitle = roleResultSet.getString("role");
        return new Role(roleId, roleTitle);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
  private void addRoleToUser(User user, Role role) {
    try {
      PreparedStatement insertStatement = connection
              .prepareStatement("INSERT INTO user_role values (null,  ?, ?)");
      insertStatement.setInt(1, user.getId().intValue());
      insertStatement.setInt(2, role.getId().intValue());
      insertStatement.executeUpdate();
    } catch (SQLException e) {
    }
  }

  @Override
  public void setRoleOfUser(User user, Role role) {
    deleteRolesOfUser(user);
    addRoleToUser(user,role);
  }

  @Override
  public void deleteRolesOfUser(User user) {
    try {
      PreparedStatement insertStatement = connection
              .prepareStatement("DELETE FROM user_role where `user_id` = ?");
      insertStatement.setInt(1, user.getId().intValue());
      insertStatement.executeUpdate();
    } catch (SQLException e) {

    }
  }

  @Override
  public void deleteAllUserRoles() {
    try {
      Statement statement = connection.createStatement();
      String sql = "DELETE from user_role where id >= 0";
      statement.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
