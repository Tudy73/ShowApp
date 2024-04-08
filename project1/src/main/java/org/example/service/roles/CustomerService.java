package org.example.service.roles;

import org.example.model.product.Pile;
import org.example.model.product.Product;
import org.example.model.security.User;
import org.example.repository.OrderRepository;
import org.example.repository.product.ProductRepository;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class CustomerService implements MyService{
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;

    public CustomerService(UserRepository userRepository, ProductRepository productRepository, RoleRepository roleRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
    }

    public List<Pile> findProductsForUser(User user) {
        return productRepository.findCart(user.getId());
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public void incrementProductForUser(long user_id, long product_id) {
        productRepository.incrementProductInCart(user_id,product_id);
    }

    public void decrementProductForUser(long user_id, long product_id) {
        productRepository.decrementProductFromCart(user_id,product_id);
    }

    public boolean orderNotFound(Long id) {
        return !orderRepository.findOrder(id);
    }

    public void addOrder(Long id) {
        try {
            orderRepository.addOrder(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
