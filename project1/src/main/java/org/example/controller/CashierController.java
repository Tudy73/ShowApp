package org.example.controller;

import lombok.Getter;
import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.model.validation.Notification;
import org.example.service.roles.CashierService;
import org.example.view.CashierView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.example.database.Constants.CASHIER;
import static org.example.database.Constants.CUSTOMER;

public class CashierController {
    @Getter
    private final CashierView view;
    private final CashierService cashierService;
    private final User user;
    public CashierController(CashierView view, CashierService cashierService, User user) {
        this.view = view;
        this.cashierService = cashierService;
        this.user = user;
        view.setRegisterListener(new RegisterButtonListener());
        view.setCompleteOrderListener(new CompleteOrderButtonListener());
        view.setExecuteListener(new ExecuteButtonListener());
        updateCustomersTable();
    }

    public void updateCustomersTable(){
        List<User> list = cashierService.findAllCustomers();
        view.updateCustomerList(list);
    }

    public class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserButton userButton = (UserButton) e.getSource();
            Notification<User> registerNotification = cashierService.register(userButton.getUsername(),userButton.getPassword());
            if (registerNotification.hasErrors()) {
                JOptionPane.showMessageDialog(view.getContentPane(), registerNotification.getFormattedErrors());
            } else {
                updateCustomersTable();
            }
        }
    }
    public class CompleteOrderButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserButton userButton = (UserButton) e.getSource();
            User user1 = User.builder()
                    .id(userButton.getId())
                    .role(new Role(3L, CUSTOMER))
                    .username(userButton.getUsername())
                    .money(userButton.getMoney())
                    .points(userButton.getPoints())
                    .build();
            try{
                cashierService.completeOrder(user1, user);
            }
            catch(RuntimeException er){
                JOptionPane.showMessageDialog(view.getContentPane(), er.getMessage());
            }
            updateCustomersTable();
        }
    }
    public class ExecuteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserButton userButton = (UserButton) e.getSource();
            User user = User.builder()
                    .id(userButton.getId())
                    .role(new Role(3L, CUSTOMER))
                    .username(userButton.getUsername())
                    .money(userButton.getMoney())
                    .points(userButton.getPoints())
                    .build();
            try {
                cashierService.updateUser(user);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(view.getContentPane(), "Couldn't update user");
            }
            updateCustomersTable();
        }
    }
}
