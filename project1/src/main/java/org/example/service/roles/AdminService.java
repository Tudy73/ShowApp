package org.example.service.roles;

import org.example.model.product.Report;
import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.model.validation.Notification;
import org.example.repository.ConfigRepository;
import org.example.repository.product.ProductRepository;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.UserRepository;
import org.example.service.security.SecurityService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminService extends UserService implements MyService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;
    private final ConfigRepository configRepository;

    public AdminService(UserRepository userRepository, ProductRepository productRepository, RoleRepository roleRepository, ConfigRepository configRepository) {
        super(userRepository, roleRepository,configRepository);
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.configRepository = configRepository;
    }

    public void updateCashier(User user) throws SQLException {
        super.updateUser(user);
    }

    public Notification<Report> getReport(long user_id) {
        return userRepository.findReport(user_id);
    }

    public List<User> findAllCashiers() {
        return super.findAllUsersWithRole(ERole.CASHIER);
    }

    public Notification<User> register(String username, String password) {
        return super.register(User.builder()
                .username(username)
                .password(password)
                .role(roleRepository.findRoleByTitle(ERole.CASHIER))
                .build());
    }
}
