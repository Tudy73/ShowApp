package org.example.controller;

import org.example.model.security.ERole;
import org.example.model.security.User;
import org.example.model.validation.Notification;
import org.example.service.roles.AdminService;
import org.example.service.roles.CashierService;
import org.example.service.roles.CustomerService;
import org.example.service.roles.MyService;
import org.example.service.security.SecurityService;
import org.example.view.AdminView;
import org.example.view.CashierView;
import org.example.view.CustomerView;
import org.example.view.LoginView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

import static org.example.database.Constants.*;

public class LoginController {

    private final LoginView view;
    private final SecurityService securityService;


    public LoginController(LoginView view, SecurityService securityService, List<MyService> myServiceList) {
        this.view = view;
        this.securityService = securityService;
        this.view.setLoginButtonListener(new LoginButtonListener(myServiceList));
        this.view.setGuestButtonListener(new GuestButtonListener((CustomerService) myServiceList.get(ERole.CUSTOMER.getValue())));
    }

    private class LoginButtonListener implements ActionListener {
        private final List<MyService> myServiceList;

        public LoginButtonListener(List<MyService> myServiceList) {
            this.myServiceList = myServiceList;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            String password = view.getPassword();
            Notification<User> res = securityService.login(username, password);
            if (!res.hasErrors()) {
                JOptionPane.showMessageDialog(view.getContentPane(), LOGIN_SUCCESSFUL);
                switch (res.getResult().getRole().getRole()) {
                    case CASHIER:
                        CashierService cashierService = (CashierService) (myServiceList.get(ERole.CASHIER.getValue()));
                        CashierController cashierController = new CashierController(new CashierView(), cashierService, res.getResult());
                        cashierController.getView().setVisible();
                        break;
                    case CUSTOMER:
                        CustomerService customerService = (CustomerService) (myServiceList.get(ERole.CUSTOMER.getValue()));
                        CustomerController customerController = new CustomerController(new CustomerView(), customerService, Optional.of(res.getResult()));
                        customerController.getView().setVisible();
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(view.getContentPane(), res.getFormattedErrors());
            }
        }
    }
    private class GuestButtonListener implements ActionListener {
        private final CustomerService customerService;

        private GuestButtonListener(CustomerService customerService) {
            this.customerService = customerService;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CustomerController customerController = new CustomerController(new CustomerView(), customerService, Optional.empty());
            customerController.getView().setVisible();
        }
    }
}
