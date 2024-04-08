package org.example.repository.security;

import org.example.model.product.Report;
import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.model.validation.Notification;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
  Notification<User> findByUsernameAndPassword(String username, String password);

  User create(User user) throws SQLException;

  void deleteAll();
  void delete(long user_id);

  List<User> findAll();

  void update(User user) throws SQLException;

  Notification<User> findById(Long id);

  List<User> findByRole(ERole role);

  Notification<Report> findReport(long user_id);

  void updateReport(long user_id, Report report);

}
