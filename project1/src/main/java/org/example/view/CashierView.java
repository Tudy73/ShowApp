package org.example.view;

import lombok.Getter;
import lombok.Setter;
import org.example.controller.CashierController;
import org.example.controller.UserButton;
import org.example.model.security.ERole;
import org.example.model.security.Role;
import org.example.model.security.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import static javax.swing.BoxLayout.Y_AXIS;

public class CashierView extends JFrame {
    private JPanel customerListPanel;
    @Setter
    private ActionListener registerListener, completeOrderListener, executeListener;
    Dimension defaultDimension = new Dimension(100, 20);

    public CashierView() throws HeadlessException {
        setSize(700, 600);
        setLocationRelativeTo(null);
        setTitle("Cashier Page");
        ;
        setLocation(50, 0);
        initializeFields();
        setLayout(new BoxLayout(getContentPane(), Y_AXIS));
        add(customerListPanel);
    }

    private void initializeFields() {
        customerListPanel = new JPanel();
    }

    public void setVisible() {
        this.setVisible(true);
    }

    public void updateCustomerList(List<User> validList) {
        customerListPanel.removeAll();
        customerListPanel.setLayout(new GridLayout(0, 1));
        validList.add(User.builder().role(new Role(3L, ERole.CUSTOMER.toString())).build());

        HeadPanel headPanel = new HeadPanel(List.of("Id", "Name", "Money", "Points"));
        customerListPanel.add(headPanel);

        for (User user : validList) {
            JPanel userPanel = new JPanel();
            userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JTextField nameLabel = new JTextField(user.getUsername());
            nameLabel.setPreferredSize(defaultDimension);

            if (user.getUsername() == null) {
                JTextField passLabel = new JTextField(user.getPassword());
                passLabel.setPreferredSize(defaultDimension);
                userPanel.add(nameLabel);
                userPanel.add(passLabel);
                customerListPanel.add(new HeadPanel(List.of("Name", "Password")));
                UserButton userButton = UserButton.builder()
                        .name("Register")
                        .username(nameLabel)
                        .password(passLabel)
                        .build();
                userButton.addActionListener(registerListener);
                userPanel.add(userButton);
            } else {
                JTextField pointsLabel = new JTextField(user.getPoints().toString());
                pointsLabel.setPreferredSize(defaultDimension);
                JTextField moneyLabel = new JTextField(user.getMoney().toString());
                moneyLabel.setPreferredSize(defaultDimension);
                JLabel idLabel = new JLabel(String.valueOf(user.getId()));
                idLabel.setPreferredSize(defaultDimension);
                userPanel.add(idLabel);
                userPanel.add(nameLabel);
                userPanel.add(moneyLabel);
                userPanel.add(pointsLabel);
                UserButton completeOrderButton = UserButton.builder()
                        .id(idLabel)
                        .username(nameLabel)
                        .money(moneyLabel)
                        .points(pointsLabel)
                        .name("Complete Order")
                        .build();
                completeOrderButton.addActionListener(completeOrderListener);
                UserButton executeButton = UserButton.builder()
                        .name("Execute")
                        .username(nameLabel)
                        .money(moneyLabel)
                        .points(pointsLabel)
                        .id(idLabel)
                        .build();
                executeButton.addActionListener(executeListener);
                userPanel.add(completeOrderButton);
                userPanel.add(executeButton);
            }
            customerListPanel.add(userPanel);
        }
        customerListPanel.revalidate(); // Refresh the panel layout with the new components
        customerListPanel.repaint(); // Ensure the panel is repainted
    }
}
