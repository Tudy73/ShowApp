package org.example.repository;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.RoleRepositorySQL;
import org.example.repository.security.UserRepository;
import org.example.repository.security.UserRepositorySQL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.example.database.SupportedDatabase.MYSQL;
import static org.example.model.security.ERole.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderRepositorySQLTest {
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private User foundUser;
    private User user2;
    private Role cashier, customer, administrator;
    private final String mail = "tudor1@cashier.com";
    private final String pass = "S!1tudorica";
    private final String mail1 = "tudor@customer.com";



    @BeforeAll
    public void setupClass() {
        DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
        roleRepository = new RoleRepositorySQL(connectionWrapper.getConnection());
        userRepository = new UserRepositorySQL(connectionWrapper.getConnection(), roleRepository);
        orderRepository = new OrderRepositorySQL(connectionWrapper.getConnection());
        customer = roleRepository.findRoleByTitle(CUSTOMER);
        cashier = roleRepository.findRoleByTitle(CASHIER);
        administrator = roleRepository.findRoleByTitle(ADMINISTRATOR);
    }
    @BeforeEach
    void setup() throws SQLException {
        userRepository.deleteAll();
        roleRepository.deleteAllUserRoles();
        orderRepository.deleteAll();
        foundUser = User.builder()
                .username(mail)
                .password(pass)
                .role(customer)
                .points(1)
                .build();
        userRepository.create(foundUser);
        user2 = User.builder()
                .username("tudorica")
                .password("marcel")
                .role(cashier)
                .points(0)
                .build();
        userRepository.create(user2);
    }
    @Test
    public void deleteForUser() throws SQLException {
        long id = foundUser.getId();
        orderRepository.addOrder(id);
        assertTrue(orderRepository.findOrder(id));
        orderRepository.    deleteForUser(id);
        assertFalse(orderRepository.findOrder(id));
    }
    @Test
    public void findOrderSuccess() throws SQLException {
        orderRepository.addOrder(foundUser.getId());
        assertTrue(orderRepository.findOrder(foundUser.getId()));
    }
    @Test
    public void findOrderFail(){
        assertFalse(orderRepository.findOrder(foundUser.getId()+1));
    }

    @Test
    public void addOrderDuplicateUserId() throws SQLException {
        orderRepository.addOrder(foundUser.getId());
        assertThrows(SQLException.class, () -> orderRepository.addOrder(foundUser.getId()));
    }

    @Test
    public void deleteAll() throws SQLException {
        orderRepository.addOrder(foundUser.getId());
        orderRepository.addOrder(user2.getId());

        assertTrue(orderRepository.findOrder(foundUser.getId()));
        assertTrue(orderRepository.findOrder(user2.getId()));

        orderRepository.deleteAll();

        assertFalse(orderRepository.findOrder(foundUser.getId()));
        assertFalse(orderRepository.findOrder(user2.getId()));
    }
}
