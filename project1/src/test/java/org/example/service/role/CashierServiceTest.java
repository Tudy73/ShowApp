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
import org.example.service.roles.CashierService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.example.database.Constants.PERCENTAGE;
import static org.example.database.SupportedDatabase.MYSQL;
import static org.example.model.security.ERole.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CashierServiceTest {
    public static final String MAIL = "tudor11@admin.com";
    public static final String PASS = "S!1tudorica";
    User user1,user2;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private CashierService cashierService;
    private ConfigRepository configRepository;
    private Role cashier, customer, administrator;
    private final String mail = "tudor1@cashier.com";
    private final String pass = PASS;
    @BeforeAll
    public void setupClass() {
        DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
        roleRepository = new RoleRepositorySQL(connectionWrapper.getConnection());
        userRepository = new UserRepositorySQL(connectionWrapper.getConnection(), roleRepository);
        orderRepository = new OrderRepositorySQL(connectionWrapper.getConnection());
        productRepository = new ProductRepositorySQL(connectionWrapper.getConnection());
        configRepository = new ConfigRepositorySQL(connectionWrapper.getConnection());
        cashierService = new CashierService(userRepository,productRepository,roleRepository,orderRepository,configRepository);
        customer = roleRepository.findRoleByTitle(CUSTOMER);
        cashier = roleRepository.findRoleByTitle(CASHIER);
        administrator = roleRepository.findRoleByTitle(ADMINISTRATOR);
    }
    @BeforeEach
    void setup() throws SQLException {
        userRepository.deleteAll();
        roleRepository.deleteAllUserRoles();
        orderRepository.deleteAll();
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
    public void completeOrder() throws SQLException {
        productRepository.incrementProductInCart(user1.getId(),1L);
        productRepository.incrementProductInCart(user1.getId(),2L);
        productRepository.incrementProductInCart(user1.getId(),3L);
        orderRepository.addOrder(user1.getId());
        cashierService.completeOrder(user1,user2);
        assertEquals(0,productRepository.findCart(user1.getId()).size());
        assertFalse(orderRepository.findOrder(user1.getId()));
        Report report = userRepository.findReport(user2.getId()).getResult();
        double cost = 10.99+9.99+11.99;
        assertEquals(cost,report.getTotalMoney());
        assertEquals(3L,report.getSoldItems());
        assertEquals(1L,report.getNoCustomers());
        double initMoney = 100.0;
        int initPoints = 1;
        user1 = userRepository.findById(user1.getId()).getResult();
        double percentage = configRepository.findValue(PERCENTAGE);
        assertEquals(initMoney - ((1-percentage/100)*cost - initPoints),user1.getMoney());
        assertEquals((int)(cost*(percentage/100)),user1.getPoints());
    }
    @Test
    public void updateUser(){
        //Tested for UserService
    }
    @Test
    public void findAllCustomers(){
        //Tested for UserService
    }
    @Test
    public void register(){
        //Most cases tested in SecurityService
        cashierService.register(MAIL, PASS);
        assertFalse(cashierService.login(MAIL, PASS).hasErrors());
        assertEquals(customer.getRole(),
                cashierService.login(MAIL, PASS).getResult().getRole().getRole());
    }
}
