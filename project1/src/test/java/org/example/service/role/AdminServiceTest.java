package org.example.service.role;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.model.product.Report;
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
import org.example.service.roles.AdminService;
import org.example.service.roles.CashierService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.example.database.Constants.ERRORS.NO_REPORT_FOUND;
import static org.example.database.SupportedDatabase.MYSQL;
import static org.example.model.security.ERole.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminServiceTest {
    public static final String MAIL = "tudor11@admin.com";
    public static final String PASS = "S!1tudorica";
    User user1, user2;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private ConfigRepository configRepository;
    private AdminService adminService;
    private Role cashier, customer, administrator;
    private final String mail = "tudor1@cashier.com";
    private final String pass = PASS;

    @BeforeAll
    public void setupClass() {
        DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
        roleRepository = new RoleRepositorySQL(connectionWrapper.getConnection());
        userRepository = new UserRepositorySQL(connectionWrapper.getConnection(), roleRepository);
        productRepository = new ProductRepositorySQL(connectionWrapper.getConnection());
        configRepository = new ConfigRepositorySQL(connectionWrapper.getConnection());
        adminService = new AdminService(userRepository, productRepository, roleRepository,configRepository);
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
    public void updateCashier() {
        //Tested for UserService
    }

    @Test
    public void getReport() {
        Report report = Report.builder()
                .soldItems(5L)
                .noCustomers(3L)
                .totalMoney(33.2).build();
        userRepository.updateReport(user1.getId(),report);
        Report found = adminService.getReport(user1.getId()).getResult();
        assertEquals(report.getSoldItems(),found.getSoldItems());
        assertEquals(report.getTotalMoney(),found.getTotalMoney());
        assertEquals(report.getNoCustomers(),found.getNoCustomers());
        assertEquals(NO_REPORT_FOUND,adminService.getReport(user2.getId()).getFormattedErrors());
    }

    @Test
    public void findAllCashiers() {
        //Tested for UserService
    }

    @Test
    public void register() {
        //Most cases tested in SecurityService
        adminService.register(MAIL, PASS);
        assertFalse(adminService.login(MAIL, PASS).hasErrors());
        assertEquals(cashier.getRole(),
                adminService.login(MAIL, PASS).getResult().getRole().getRole());

    }
}
