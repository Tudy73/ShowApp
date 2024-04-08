package org.example.service.roles;

import org.example.model.product.Pile;
import org.example.model.product.Report;
import org.example.model.security.ERole;
import org.example.model.security.User;
import org.example.model.validation.Notification;
import org.example.repository.ConfigRepository;
import org.example.repository.OrderRepository;
import org.example.repository.product.ProductRepository;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.UserRepository;

import java.sql.SQLException;
import java.util.List;

import static org.example.database.Constants.PERCENTAGE;

public class CashierService extends UserService implements MyService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final ConfigRepository configRepository;

    public CashierService(UserRepository userRepository, ProductRepository productRepository, RoleRepository roleRepository, OrderRepository orderRepository, ConfigRepository configRepository) {
        super(userRepository, roleRepository,configRepository);
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
        this.configRepository = configRepository;
    }

    public void completeOrder(User user, User cashier) {
        List<Pile> cart = productRepository.findCart(user.getId());
        double percentage = configRepository.findValue(PERCENTAGE);
        if(cart.isEmpty()){
            throw new RuntimeException("Cart is empty");
        }
        double money = 0;
        long items = 0;
        for(Pile pile : cart){
            items+=pile.getAmount();
            money += pile.getAmount() * pile.getProduct().getPrice();
        }
        double percentageLeft = 1 - percentage / 100;
        if(user.getPoints() >= money){
            user.setPoints((int) (user.getPoints() - money* percentageLeft));
            try {
                userRepository.update(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else if(user.getMoney() >= money* percentageLeft - user.getPoints()){
            user.setMoney(user.getMoney() - (money* percentageLeft - user.getPoints()));
            user.setPoints((int)(money*(percentage/100)));
            try {
                userRepository.update(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            throw new RuntimeException("Not enough money");
        }

        Report report = Report.builder()
                .soldItems(items)
                .totalMoney(money)
                .noCustomers(1L)
                .build();
        userRepository.updateReport(cashier.getId(),report);
        productRepository.deleteCart(user.getId());
        orderRepository.deleteForUser(user.getId());
    }

    public void updateUser(User user) throws SQLException {
        super.updateUser(user);
    }

    public List<User> findAllCustomers() {
        return super.findAllUsersWithRole(ERole.CUSTOMER);
    }

    public Notification<User> register(String username, String password) {
        return super.register(User.builder()
                .username(username)
                .password(password)
                .role(roleRepository.findRoleByTitle(ERole.CUSTOMER))
                .build());
    }
}
