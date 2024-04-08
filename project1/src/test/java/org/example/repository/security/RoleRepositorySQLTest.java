package org.example.repository.security;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.example.database.SupportedDatabase.MYSQL;
import static org.example.model.security.ERole.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleRepositorySQLTest {
    private final String mail = "tudor@cashier.com";
    private final String pass = "S!1tudorica";
    private Role cashier, customer, administrator;
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    @BeforeAll
    public void setupClass() {
        DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
        roleRepository = new RoleRepositorySQL(connectionWrapper.getConnection());
        userRepository = new UserRepositorySQL(connectionWrapper.getConnection(), roleRepository);
        customer = roleRepository.findRoleByTitle(CUSTOMER);
        cashier = roleRepository.findRoleByTitle(CASHIER);
        administrator = roleRepository.findRoleByTitle(ADMINISTRATOR);
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void createFail(){
        //doesn't throw anything + it is not used appart from bootstrapping and there it works
    }

    @Test//OK
    public void findRoleByTitle() {
        assertEquals(CASHIER.toString(), roleRepository.findRoleByTitle(CASHIER).getRole());
        assertEquals(ADMINISTRATOR.toString(), roleRepository.findRoleByTitle(ADMINISTRATOR).getRole());
        assertEquals(CUSTOMER.toString(), roleRepository.findRoleByTitle(CUSTOMER).getRole());
    }

    @Test//OK
    public void findRoleForUser() throws SQLException {
        User sentUser = User.builder()
                .username(mail)
                .password(pass)
                .role(cashier)
                .build();
        User user = userRepository.create(sentUser);
        Role role = roleRepository.findRoleForUser(user.getId());
        assertEquals(CASHIER.toString(), role.getRole());
    }

    @Test//OK
    public void setRoleOfUser() throws SQLException {
        User sentUser = User.builder()
                .username(mail)
                .password(pass)
                .role(cashier)
                .build();
        User user = userRepository.create(sentUser);
        assertEquals(CASHIER.toString(), user.getRole().getRole());
        roleRepository.setRoleOfUser(user,new Role(1L,ADMINISTRATOR.toString()));
        assertEquals(ADMINISTRATOR.toString(), roleRepository.findRoleForUser(user.getId()).getRole());
    }

    @Test//OK
    public void deleteRolesOfUser() throws SQLException {
        User sentUser = User.builder()
                .username(mail)
                .password(pass)
                .role(cashier)
                .build();
        User user = userRepository.create(sentUser);
        roleRepository.deleteRolesOfUser(user);
        assertNull(roleRepository.findRoleForUser(user.getId()));
    }

}
