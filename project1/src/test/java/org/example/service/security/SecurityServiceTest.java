package org.example.service.security;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.model.validation.Notification;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.RoleRepositorySQL;
import org.example.repository.security.UserRepository;
import org.example.repository.security.UserRepositorySQL;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.example.database.Constants.ERRORS.NO_USER_FOUND;
import static org.example.database.SupportedDatabase.MYSQL;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityServiceTest {

  public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password.";
  private SecurityService securityService;
  private UserRepository userRepository;
  private final String mail = "johndoe@admin.com";
  private final String pass = "S!1tudorica";

  @BeforeEach
  @AfterEach
  public void cleanup() {
    userRepository.deleteAll();
  }

  @BeforeAll
  public void setup() {
    DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
    Connection connection = connectionWrapper.getConnection();

    RoleRepository roleRepositorySQL = new RoleRepositorySQL(connection);
    userRepository = new UserRepositorySQL(connection, roleRepositorySQL);
    securityService = new SecurityService(userRepository, roleRepositorySQL);
  }

  @Test//OK
  void register() {
    String johndoe = mail;
    User user = User.builder().username(mail).password(pass)
            .role(new Role(1L, ERole.ADMINISTRATOR.toString())).build();
    Notification<User> user1 = securityService.register(user);
    User result = user1.getResult();
    assertNotNull(result);
    assertTrue(result.getId() >= 0);
    User userNew = User.builder().username(mail).password(pass+1)
            .role(new Role(1L, ERole.ADMINISTRATOR.toString())).build();
    Notification<User> user2 = securityService.register(userNew);
    assertThrows(Exception.class, user2::getResult);
  }
  @Test//OK
  void login(){
    User user = User.builder().username(mail).password(pass)
            .role(new Role(1L, ERole.ADMINISTRATOR.toString())).build();
    Notification<User> user1 = securityService.register(user);
    assertEquals(user1.getResult().getId(),securityService.login(mail,pass).getResult().getId());
    assertEquals(NO_USER_FOUND,securityService.login(mail+1,pass).getFormattedErrors());
  }
}