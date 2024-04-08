package org.example.service.role;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.repository.ConfigRepository;
import org.example.repository.ConfigRepositorySQL;
import org.example.repository.OrderRepository;
import org.example.repository.OrderRepositorySQL;
import org.example.repository.product.ProductRepository;
import org.example.repository.product.ProductRepositorySQL;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.RoleRepositorySQL;
import org.example.repository.security.UserRepository;
import org.example.repository.security.UserRepositorySQL;
import org.example.service.roles.CustomerService;
import org.example.service.roles.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.List;

import static org.example.database.SupportedDatabase.MYSQL;
import static org.example.model.security.ERole.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    User user1,user2;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private ConfigRepository configRepository;
    private UserService userService;
    private Role cashier, customer, administrator;
    private final String mail = "tudor1@cashier.com";
    private final String pass = "S!1tudorica";

    @BeforeAll
    public void setupClass() {
        DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
        roleRepository = new RoleRepositorySQL(connectionWrapper.getConnection());
        userRepository = new UserRepositorySQL(connectionWrapper.getConnection(), roleRepository);
        configRepository = new ConfigRepositorySQL(connectionWrapper.getConnection());
        userService = new UserService(userRepository,roleRepository,configRepository);
        customer = roleRepository.findRoleByTitle(CUSTOMER);
        cashier = roleRepository.findRoleByTitle(CASHIER);
        administrator = roleRepository.findRoleByTitle(ADMINISTRATOR);
    }
    @BeforeEach
    void setup() throws SQLException {
        userRepository.deleteAll();
        roleRepository.deleteAllUserRoles();
        user1 = User.builder()
                .username(mail)
                .password(pass)
                .role(customer)
                .points(1)
                .build();
        userRepository.create(user1);
        user2 = User.builder()
                .username("tudorica")
                .password("marcel")
                .role(cashier)
                .points(0)
                .build();
        userRepository.create(user2);
    }

    @Test
    public void updateUser() throws SQLException {
        user1.setUsername("tudor");
        user1.setPassword("tudor");
        user1.setPoints(2);
        user1.setRole(cashier);
        userService.updateUser(user1);
        User foundUser = userRepository.findById(user1.getId()).getResult();
        assertEquals("tudor",foundUser.getUsername());
        assertEquals(2,foundUser.getPoints());
        assertEquals(cashier.getRole(),foundUser.getRole().getRole());
        user1.setUsername("tudorica");
        assertThrows(SQLException.class,()->userService.updateUser(user1));

    }
    @Test
    public void findAllUsersWithRole() throws SQLException {
        user1 = User.builder()
                .username(1 + mail)
                .password(pass)
                .role(cashier)
                .points(1)
                .build();
        userRepository.create(user1);
        List<User> list = userService.findAllUsersWithRole(CASHIER);
        assertEquals(1 + mail,list.get(1).getUsername());
        assertEquals("tudorica",list.get(0).getUsername());

        assertTrue(userService.findAllUsersWithRole(ADMINISTRATOR).isEmpty());
    }

}
