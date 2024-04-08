package org.example.view;

import lombok.Getter;
import lombok.Setter;
import org.example.controller.AdminController;
import org.example.controller.ProductButton;
import org.example.controller.UserButton;
import org.example.model.security.Role;
import org.example.model.security.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import static javax.swing.BoxLayout.Y_AXIS;
import static org.example.database.Constants.PERCENTAGE;
import static org.example.model.security.ERole.CASHIER;

@Getter
public class AdminView extends JFrame {
    private JPanel cashiersListPanel;
    private JPanel percentagePanel;
    Dimension defaultDimension = new Dimension(150, 20);
    Dimension smallDimension = new Dimension(30, 20);

    private JButton registerButton,percentageButton;
    private JTextField registerName, registerPass;

    @Setter
    ActionListener generateListener, executeListener, deleteListener;


    public AdminView() throws HeadlessException {
        setSize(650, 500);
        setLocationRelativeTo(null);
        setLocation(610, 0);
        setTitle("Admin Page");
        initializeFields();
        setLayout(new BoxLayout(getContentPane(), Y_AXIS));
        add(percentagePanel);
        add(cashiersListPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initializeFields() {
        cashiersListPanel = new JPanel();
        percentagePanel = new JPanel();
        registerButton = new JButton("Register");
        registerName = new JTextField();
        registerPass = new JTextField();
        registerName.setPreferredSize(defaultDimension);
        registerPass.setPreferredSize(defaultDimension);
    }
    public void initPercentagePanel(double percentage) {
        percentagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        Dimension textFieldSize = new Dimension(150, 20);
        JLabel percentageTextLabel = new JLabel("Percentage: ");
        percentageTextLabel.setPreferredSize(textFieldSize);
        JTextField percentageText = new JTextField(String.valueOf(percentage));
        percentageText.setPreferredSize(textFieldSize);
        percentageButton = UserButton.builder()
                .money(percentageText)
                .name("Update")
                .build();
        percentagePanel.add(percentageTextLabel);
        percentagePanel.add(percentageText);
        percentagePanel.add(percentageButton);
    }

    public void setVisible() {
        this.setVisible(true);
    }


    public void updateCashiersList(List<User> list) {
        cashiersListPanel.removeAll();
        cashiersListPanel.setLayout(new GridLayout(0, 1));
        list.add(User.builder().role(new Role(2L, CASHIER.toString())).build());
        HeadPanel headPanel = new HeadPanel(List.of("Id", "Mail"));
        cashiersListPanel.add(headPanel);

        for (User user : list) {
            JPanel userPanel = new JPanel();
            userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JTextField nameLabel = new JTextField(user.getUsername());
            nameLabel.setPreferredSize(defaultDimension);
            JLabel id = new JLabel(String.valueOf(user.getId()));
            id.setPreferredSize(smallDimension);
            if (user.getUsername() == null) {
                userPanel.add(registerName);
                userPanel.add(registerPass);
                userPanel.add(registerButton);
            } else {
                userPanel.add(id);
                userPanel.add(nameLabel);
                UserButton generateReportButton = UserButton.builder()
                        .name("Report")
                        .id(id)
                        .username(nameLabel)
                        .build();
                generateReportButton.addActionListener(generateListener);

                UserButton deleteButton = UserButton.builder()
                        .name("Delete")
                        .id(id)
                        .username(nameLabel)
                        .build();
                UserButton executeButton = UserButton.builder()
                        .name("Execute")
                        .id(id)
                        .username(nameLabel)
                        .build();
                id.setVisible(true);
                executeButton.addActionListener(executeListener);
                deleteButton.addActionListener(deleteListener);
                userPanel.add(generateReportButton);
                userPanel.add(executeButton);
                userPanel.add(deleteButton);
            }
            cashiersListPanel.add(userPanel);
        }
        cashiersListPanel.revalidate(); // Refresh the panel layout with the new components
        cashiersListPanel.repaint();
    }

    public String getRegisterName() {
        return registerName.getText();
    }

    public String getRegisterPass() {
        return registerPass.getText();
    }

    public void setRegisterButtonListener(ActionListener registerButtonListener) {
        registerButton.addActionListener(registerButtonListener);
    }
    public void setPercentageListener(ActionListener actionListener){
        percentageButton.addActionListener(actionListener);
    }

}
