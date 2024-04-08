package org.example.controller;

import lombok.Getter;
import org.example.model.product.Report;
import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;
import org.example.model.validation.Notification;
import org.example.service.roles.AdminService;
import org.example.service.roles.CashierService;
import org.example.view.AdminView;
import org.example.view.ReportView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import static org.example.database.Constants.CASHIER;
import static org.example.database.Constants.PERCENTAGE;

public class AdminController {
    @Getter
    private final AdminView view;
    private final AdminService adminService;

    public AdminController(AdminView adminView, AdminService adminService) {
        this.view = adminView;
        this.adminService = adminService;
        view.setRegisterButtonListener(new RegisterButtonListener());
        view.setGenerateListener(new GenerateReportButtonListener());
        view.setDeleteListener(new DeleteButtonListener());
        view.setExecuteListener(new ExecuteButtonListener());
        setPercentagePanel();
        view.setPercentageListener(new PercentageChangeButtonListener());
        updateCashiersList();
    }

    private void setPercentagePanel() {
        double percentage;
        try {
            percentage = adminService.findSetting(PERCENTAGE);
        }
        catch (RuntimeException e){
            percentage = 10.0;
            adminService.updateSetting(PERCENTAGE,percentage);
        }
        view.initPercentagePanel(percentage);
    }

    public void updateCashiersList(){
        List<User> list = adminService.findAllCashiers();
        view.updateCashiersList(list);
    }
    public class PercentageChangeButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            UserButton button = (UserButton) e.getSource();
            double percentage = button.getMoney();
            if (percentage > 100){
                percentage =100;
            }
            adminService.updateSetting(PERCENTAGE,percentage);
            JOptionPane.showMessageDialog(view.getContentPane(), "The percentage is set to " + percentage);
        }
    }
    public class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getRegisterName();
            String password = view.getRegisterPass();
            Notification<User> registerNotification = adminService.register(username,password);
            if (registerNotification.hasErrors()) {
                JOptionPane.showMessageDialog(view.getContentPane(), registerNotification.getFormattedErrors());
            } else {
                updateCashiersList();
            }
        }
    }
    public class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserButton userButton = (UserButton)e.getSource();
            adminService.deleteUser(userButton.getId());
            updateCashiersList();
        }
    }
    public class ExecuteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserButton userButton = (UserButton) e.getSource();
            User user = User.builder()
                    .id(userButton.getId())
                    .role(new Role(2L, CASHIER))
                    .username(userButton.getUsername())
                    .build();
            try {
                adminService.updateCashier(user);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(view.getContentPane(), "Couldn't update user");
            }
            updateCashiersList();
        }
    }
    public class GenerateReportButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UserButton userButton = (UserButton) e.getSource();
            Notification<Report> report = adminService.getReport(userButton.getId());
            if(!report.hasErrors()){
                new ReportView(report.getResult()).setVisible();
            }
            else{
                JOptionPane.showMessageDialog(view.getContentPane(), "No completed orders");
            }
        }
    }
}
