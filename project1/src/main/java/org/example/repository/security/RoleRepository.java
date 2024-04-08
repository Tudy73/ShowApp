package org.example.repository.security;

import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;

public interface RoleRepository {
  void create(ERole role);

  Role findRoleByTitle(ERole role);

Role findRoleForUser(long userId);

  void setRoleOfUser(User user, Role role);

  void deleteRolesOfUser(User user);

  void deleteAllUserRoles();
}
