package org.example.repository.security;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.model.product.Report;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.model.validation.Notification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.example.database.Constants.ERRORS.*;
import static org.example.database.SupportedDatabase.MYSQL;
import static org.example.model.security.ERole.*;
import static org.example.model.security.ERole.CASHIER;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositorySQLTest {
    private final String mail1 = "tudor@customer.com";

    private Role cashier, customer, administrator;
    private RoleRepository roleRepository;
    private UserRepository userRepository;

    private final String mail = "tudor1@cashier.com";
    private final String pass = "S!1tudorica";


    private User foundUser;
    private User user2;

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
    void setup() throws SQLException {
        userRepository.deleteAll();
        roleRepository.deleteAllUserRoles();
        foundUser = User.builder()
                .username(mail)
                .password(pass)
                .role(customer)
                .points(1)
                .money(153.0)
                .build();
        userRepository.create(foundUser);
        user2 = User.builder()
                .username("tudorica")
                .password("marcel")
                .role(cashier)
                .points(0)
                .build();
    }

    private void assertUserEqualsAndIdPresent(User user1, User user2) {
        assertEquals(user1.getUsername(), user2.getUsername());
        assertEquals(user1.getRole().getRole(), user2.getRole().getRole());
        assertEquals(user1.getPoints(), user2.getPoints());
        assertNotNull(user2.getId());
    }

    @Test//OK
    public void createFailDuplicateUsername() {
        assertThrows(SQLException.class, () -> userRepository.create(foundUser));
    }

    @Test//OK
    public void createSuccess() throws SQLException {
        User user = userRepository.create(user2);
        assertUserEqualsAndIdPresent(user2, user);
    }

    @Test//OK
    public void findAll() throws SQLException {
        userRepository.deleteAll();
        roleRepository.deleteAllUserRoles();
        List<User> userList = new ArrayList<>();
        assertEquals(0, userRepository.findAll().size());
        for (int i = 0; i < 3; i++) {
            User user = User.builder()
                    .username(i + mail)
                    .password(pass)
                    .role(cashier)
                    .points(0)
                    .build();
            userRepository.create(user);
            userList.add(user);
        }
        List<User> retrievedList = userRepository.findAll();
        for (int i = 0; i < 3; i++) {
            User user = retrievedList.get(i);
            User comparedUser = userList.get(i);
            assertUserEqualsAndIdPresent(comparedUser, user);
        }
    }

    @Test//OK
    public void deleteAll() throws SQLException {
        for (int i = 0; i < 3; i++) {
            User user = User.builder()
                    .username(i + mail)
                    .password(pass)
                    .role(cashier)
                    .build();
            userRepository.create(user);
        }
        assertEquals(4, userRepository.findAll().size());
        userRepository.deleteAll();
        assertEquals(0, userRepository.findAll().size());
    }

    @Test//OK
    public void findByUsernameAndPasswordSuccessful() {
        User user = userRepository.findByUsernameAndPassword(mail, pass).getResult();
        assertUserEqualsAndIdPresent(foundUser, user);
    }

    @Test//OK
    public void findByUsernameAndPasswordInvalid() {
        Notification<User> badUser = userRepository.findByUsernameAndPassword(mail1, pass);
        assertEquals(NO_USER_FOUND, badUser.getFormattedErrors());
    }

    @Test//OK
    public void delete() {
        assertEquals(mail, userRepository.findByUsernameAndPassword(mail, pass).getResult().getUsername());
        userRepository.delete(foundUser.getId());
        assertEquals(NO_USER_FOUND, userRepository.
                findByUsernameAndPassword(mail, pass).getFormattedErrors());
    }

    @Test//OK
    public void updateSuccessForAllFields() throws SQLException {
        Long id = userRepository.findByUsernameAndPassword(mail, pass).getResult().getId();
        user2.setId(id);
        userRepository.update(user2);
        User user = userRepository.findById(id).getResult();
        assertUserEqualsAndIdPresent(user2, user);
    }

    @Test//OK
    public void updateFailDuplicateNameEntry() throws SQLException {
        userRepository.create(user2);
        user2.setUsername(mail);
        assertThrows(SQLException.class, () -> userRepository.update(user2));
    }

    @Test//OK
    public void findByIdSuccessAllFields() {
        User userToSearch = userRepository.findByUsernameAndPassword(mail, pass).getResult();
        Notification<User> user = userRepository.findById(userToSearch.getId());
        assertUserEqualsAndIdPresent(foundUser, user.getResult());
    }

    @Test
    public void findByIdFail() {
        User userToSearch = userRepository.findByUsernameAndPassword(mail, pass).getResult();
        Notification<User> userFailed = userRepository.findById(-1L);
        assertEquals(NO_USER_FOUND, userFailed.getFormattedErrors());
    }

    @Test//OK
    public void findByRole() {
        List<User> result = userRepository.findByRole(CUSTOMER);
        assertUserEqualsAndIdPresent(result.get(0), foundUser);
        assertTrue(userRepository.findByRole(CASHIER).isEmpty());
    }

    @Test
    public void findReport() {
        assertEquals(NO_REPORT_FOUND, userRepository.findReport(1L).getFormattedErrors());
    }

    @Test
    public void updateReport() {
        Report report = Report.builder()
                .noCustomers(1L)
                .totalMoney(33.3)
                .soldItems(3L)
                .build();
        userRepository.updateReport(foundUser.getId(),report);
        assertEquals(report,userRepository.findReport(foundUser.getId()).getResult());
        userRepository.updateReport(foundUser.getId(),report);
        report = Report.builder()
                .noCustomers(2L)
                .totalMoney(66.6)
                .soldItems(6L)
                .build();
        assertEquals(report,userRepository.findReport(foundUser.getId()).getResult());
    }
}
