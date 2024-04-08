package org.example.launcher;

import lombok.Getter;
import org.example.controller.*;
import org.example.database.DatabaseConnectionFactory;
import org.example.database.SupportedDatabase;
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
import org.example.service.roles.CustomerService;
import org.example.service.roles.MyService;
import org.example.service.security.SecurityService;
import org.example.view.AdminView;
import org.example.view.LoginView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ComponentFactory {

  private final LoginView loginView;
  private final AdminView adminView;

  private final LoginController loginController;
  private final AdminController adminController;

  private final SecurityService securityService;
  private final AdminService adminService;
  private final CustomerService customerService;
  private final CashierService cashierService;

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final ConfigRepository configRepository;
  private final List<MyService> myServiceList;


  public ComponentFactory(SupportedDatabase supportedDatabase, Boolean componentsForTest) {
    final Connection connection = DatabaseConnectionFactory.getConnectionWrapper(supportedDatabase, componentsForTest).getConnection();
    this.roleRepository = new RoleRepositorySQL(connection);
    this.userRepository = new UserRepositorySQL(connection, roleRepository);
    this.productRepository = new ProductRepositorySQL(connection);
    this.orderRepository = new OrderRepositorySQL(connection);
    this.configRepository = new ConfigRepositorySQL(connection);

    this.securityService = new SecurityService(userRepository, roleRepository);
    this.adminService = new AdminService(userRepository,productRepository,roleRepository,configRepository);
    this.cashierService = new CashierService(userRepository,productRepository,roleRepository,orderRepository,configRepository);
    this.customerService = new CustomerService(userRepository,productRepository,roleRepository,orderRepository);

    this.loginView = new LoginView();
    this.adminView = new AdminView();

    myServiceList = new ArrayList<>();
    myServiceList.add(adminService);
    myServiceList.add(cashierService);
    myServiceList.add(customerService);
    this.loginController = new LoginController(loginView, securityService, myServiceList);
    this.adminController = new AdminController(adminView,adminService);
  }

}
