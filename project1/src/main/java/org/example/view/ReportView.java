package org.example.view;

import org.example.controller.AdminController;
import org.example.model.product.Report;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.BoxLayout.Y_AXIS;

public class ReportView extends JFrame {
    private JPanel cashierReport;

    public ReportView(Report user) throws HeadlessException {
        setSize(500, 200);
        setLocationRelativeTo(null);
        setLocation(610, 0);
        setTitle("Report Page");
        initializeFields();
        setLayout(new BoxLayout(getContentPane(), Y_AXIS));
        add(cashierReport);
        updateReport(user);
    }

    private void initializeFields() {
        cashierReport = new JPanel();
    }

    public void setVisible() {
        this.setVisible(true);
    }

    public void updateReport(Report reportGiven) {
        cashierReport.removeAll();
        cashierReport.setLayout(new GridLayout(0, 1));
        Dimension textFieldSize = new Dimension(150, 20);
        addTopRow();
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel soldLabel = new JLabel(reportGiven.getSoldItems().toString());
        soldLabel.setPreferredSize(textFieldSize);
        JLabel priceLabel = new JLabel(reportGiven.getTotalMoney().toString());
        priceLabel.setPreferredSize(textFieldSize);
        JLabel customersLabel = new JLabel(reportGiven.getNoCustomers().toString());
        customersLabel.setPreferredSize(textFieldSize);

        userPanel.add(soldLabel);
        userPanel.add(priceLabel);
        userPanel.add(customersLabel);
        cashierReport.add(userPanel);
        cashierReport.revalidate(); // Refresh the panel layout with the new components
        cashierReport.repaint();
    }
    public void addTopRow(){
        Dimension textFieldSize = new Dimension(150, 20);
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel soldLabel = new JLabel("sold items");
        soldLabel.setPreferredSize(textFieldSize);
        JLabel priceLabel = new JLabel("total money");
        priceLabel.setPreferredSize(textFieldSize);
        JLabel customersLabel = new JLabel("number of customers");
        customersLabel.setPreferredSize(textFieldSize);

        userPanel.add(soldLabel);
        userPanel.add(priceLabel);
        userPanel.add(customersLabel);
        cashierReport.add(userPanel);

    }
}
