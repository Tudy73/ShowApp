package org.example.repository.product;

import org.example.database.DatabaseConnectionFactory;
import org.example.database.DbConnection;
import org.example.model.product.Pile;
import org.example.model.product.Product;
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
import java.util.List;
import java.util.NoSuchElementException;

import static org.example.database.SupportedDatabase.MYSQL;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductRepositorySQLTest {

    private ProductRepository productRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private final String mail = "tudor@customer.com";
    private final String pass = "S!1tudorica";

    private final String[] products= {"Salt","Sugar","Milk"};

    private User user1 = User.builder()
            .username(1 + mail)
            .password(pass)
            .role(new Role(3L,"CUSTOMER"))
            .build();
    private User user2 = User.builder()
            .username(mail)
            .password(pass)
            .role(new Role(3L,"CUSTOMER"))
            .build();

    private Product salt,milk,sugar;

    private Pile saltPile,milkPile,sugarPile;


    @BeforeAll
    public void setupClass() {
        DbConnection connectionWrapper = DatabaseConnectionFactory.getConnectionWrapper(MYSQL, true);
        productRepository = new ProductRepositorySQL(connectionWrapper.getConnection());
        roleRepository = new RoleRepositorySQL(connectionWrapper.getConnection());
        userRepository = new UserRepositorySQL(connectionWrapper.getConnection(), roleRepository);
    }

    @BeforeEach
    public void init() throws SQLException {
        userRepository.deleteAll();
        userRepository.create(user1);
        salt = productRepository.findById(1L).get();
        sugar = productRepository.findById(2L).get();
        milk = productRepository.findById(3L).get();
        saltPile = Pile.builder()
                .product(salt)
                .amount(2L)
                .build();
        sugarPile = Pile.builder()
                .product(sugar)
                .amount(3L)
                .build();
        milkPile = Pile.builder()
                .product(milk)
                .amount(4L)
                .build();
    }

    @Test
//OK
    void findAll() {
        assertEquals(3, productRepository.findAll().size());
        //They are initialized in the bootstrap
    }

    @Test
//OK
    void findById() {
        assertEquals("Salt", productRepository.findById(1L).get().getName());
        assertEquals("Sugar", productRepository.findById(2L).get().getName());
        assertEquals("Milk", productRepository.findById(3L).get().getName());
        assertThrows(NoSuchElementException.class, () -> productRepository.findById(4L).get());
    }

    @Test
//OK
    void create() {
        //useless as I would need a delete method + won't use it more
    }

    @Test//OK
    void incrementProductInCart(){
        Pile pile = saltPile;
        long user_id = user1.getId();
        long prod_id = pile.getProduct().getId();
        assertNull(productRepository.findProductAndUser(user1,pile.getProduct()));
        productRepository.incrementProductInCart(user_id,prod_id);
        assertEquals(1L,productRepository.findProductAndUser(user1,pile.getProduct()).getAmount());
        productRepository.incrementProductInCart(user_id,prod_id);
        assertEquals(2L,productRepository.findProductAndUser(user1,pile.getProduct()).getAmount());
    }

    @Test//OK
    void decrementProductFromCart() {
        Pile pile = saltPile;
        long user_id = user1.getId();
        long prod_id = pile.getProduct().getId();
        productRepository.incrementProductInCart(user_id, prod_id);
        productRepository.incrementProductInCart(user_id, prod_id);
        assertEquals(2L,productRepository.findProductAndUser(user1,pile.getProduct()).getAmount());
        productRepository.decrementProductFromCart(user_id,prod_id);
        assertEquals(1L,productRepository.findProductAndUser(user1,pile.getProduct()).getAmount());
        productRepository.decrementProductFromCart(user_id,prod_id);
        assertNull(productRepository.findProductAndUser(user1,pile.getProduct()));
    }

    @Test//OK
    void findProductAndUser() {
        User user = user1;
        Pile pile = saltPile;
        long user_id = user1.getId();
        long prod_id = pile.getProduct().getId();
        productRepository.incrementProductInCart(user_id,prod_id);
        assertEquals(1L,productRepository.findProductAndUser(user,pile.getProduct()).getAmount());
        pile = milkPile;
        assertNull(productRepository.findProductAndUser(user, pile.getProduct()));
    }

    @Test//OK
    void findProductsForUser() {
        User user = user1;
        Pile pile = saltPile;
        long user_id = user1.getId();
        long prod_id = pile.getProduct().getId();
        productRepository.incrementProductInCart(user_id, prod_id);
        prod_id = sugarPile.getProduct().getId();
        productRepository.incrementProductInCart(user_id, prod_id);
        prod_id = milkPile.getProduct().getId();
        productRepository.incrementProductInCart(user_id, prod_id);
        List<Pile> piles = productRepository.findCart(user.getId());
        for(Pile pile1 : piles){
            String name = pile1.getProduct().getName();
            assertTrue(name.equals("Salt")||name.equals("Milk")||name.equals("Sugar"));
        }
    }
    @Test
    void deleteCart(){
        Pile pile = saltPile;
        long user_id = user1.getId();
        long prod_id = pile.getProduct().getId();
        productRepository.incrementProductInCart(user_id, prod_id);
        prod_id = sugarPile.getProduct().getId();
        productRepository.incrementProductInCart(user_id, prod_id);
        prod_id = milkPile.getProduct().getId();
        productRepository.incrementProductInCart(user_id, prod_id);
        productRepository.deleteCart(user_id);
        assertNull(productRepository.findProductAndUser(user1,salt));
        assertNull(productRepository.findProductAndUser(user1,milk));
        assertNull(productRepository.findProductAndUser(user1,sugar));
    }
}