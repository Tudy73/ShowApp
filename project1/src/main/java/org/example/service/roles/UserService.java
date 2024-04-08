package org.example.service.roles;

import org.example.model.security.ERole;
import org.example.model.security.User;
import org.example.model.validation.Notification;
import org.example.repository.ConfigRepository;
import org.example.repository.product.ProductRepository;
import org.example.repository.security.RoleRepository;
import org.example.repository.security.UserRepository;
import org.example.service.security.SecurityService;

import java.sql.SQLException;
import java.util.List;

public class UserService extends SecurityService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfigRepository configRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, ConfigRepository configRepository) {
        super(userRepository,roleRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.configRepository = configRepository;
    }
    public void updateUser(User user) throws SQLException{
            userRepository.update(user);
    }
    public void deleteUser(long user_id){
        userRepository.delete(user_id);
    }
    public List<User> findAllUsersWithRole(ERole erole){
        return userRepository.findByRole(erole);
    }

    public void updateSetting(String name, double value){
        configRepository.updateValue(name,value);
    }
    public double findSetting(String name){
        return configRepository.findValue(name);
    }

}
