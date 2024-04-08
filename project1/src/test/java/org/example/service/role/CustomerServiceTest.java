package org.example.service.role;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.model.product.Pile;
import org.example.model.product.Product;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.repository.OrderRepository;
import org.example.repository.OrderRepositorySQL;
import org.example.repository.product.ProductRepository;
import org.example.repository.product.ProductRepositorySQL;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.RoleRepositorySQL;
import org.example.repository.security.UserRepository;
import org.example.repository.security.UserRepositorySQL;
import org.example.service.roles.CustomerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.List;

import static org.example.database.SupportedDatabase.MYSQL;
import static org.example.database.Constants.TESTING.*;
import static org.example.model.security.ERole.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerServiceTest {
    User user1,user2;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private CustomerService customerService;
    private Role cashier, customer, administrator;
    private final String mail = "tudor1@cashier.com";
    private final String pass = "S!1tudorica";

    @BeforeAll
    public void setupClass() {
        DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
        roleRepository = new RoleRepositorySQL(connectionWrapper.getConnection());
        userRepository = new UserRepositorySQL(connectionWrapper.getConnection(), roleRepository);
        orderRepository = new OrderRepositorySQL(connectionWrapper.getConnection());
        productRepository = new ProductRepositorySQL(connectionWrapper.getConnection());
        customerService = new CustomerService(userRepository,productRepository,roleRepository,orderRepository);
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
    public void findProductsForUser() {
        List<Pile> list = customerService.findProductsForUser(user1);
        assertTrue(list.isEmpty());
        customerService.incrementProductForUser(user1.getId(),1L);
        assertEquals(1,customerService.findProductsForUser(user1).size());
        customerService.incrementProductForUser(user1.getId(),2L);
        assertEquals(2,customerService.findProductsForUser(user1).size());
    }
    @Test
    public void findAllProducts() {
        List<Product> list = customerService.findAllProducts();
        for(int i=0;i<3;i++){
            assertEquals(products[i],list.get(i).getName());
        }
    }
    @Test
    public void incrementProductForUser() {
        customerService.incrementProductForUser(user1.getId(),1L);
        customerService.incrementProductForUser(user1.getId(),2L);
        customerService.incrementProductForUser(user1.getId(),2L);
        List<Pile> productsForUser = customerService.findProductsForUser(user1);
        Pile curPile = productsForUser.get(0);
        assertEquals(SALT,curPile.getProduct().getName());
        assertEquals(1L,curPile.getAmount());
        curPile = productsForUser.get(1);
        assertEquals(SUGAR,curPile.getProduct().getName());
        assertEquals(2L,curPile.getAmount());

    }
    @Test
    public void decrementProductForUser() {
        customerService.incrementProductForUser(user1.getId(),1L);
        customerService.incrementProductForUser(user1.getId(),2L);
        customerService.incrementProductForUser(user1.getId(),2L);

        customerService.decrementProductForUser(user1.getId(),2L);
        List<Pile> productsForUser = customerService.findProductsForUser(user1);
        Pile curPile = productsForUser.get(1);
        assertEquals(1L,curPile.getAmount());

        customerService.decrementProductForUser(user1.getId(),2L);
        assertEquals(1,customerService.findProductsForUser(user1).size());
        customerService.decrementProductForUser(user1.getId(),1L);
        assertEquals(0,customerService.findProductsForUser(user1).size());
    }
    @Test
    public void orderNotFound() {
        customerService.addOrder(user1.getId());
        assertFalse(customerService.orderNotFound(user1.getId()));
        assertTrue(customerService.orderNotFound(user2.getId()+1));
    }
    @Test
    public void addOrder() {
        customerService.addOrder(user1.getId());
        assertFalse(customerService.orderNotFound(user1.getId()));
        assertThrows(RuntimeException.class,() ->customerService.addOrder(user1.getId()));
    }
}
