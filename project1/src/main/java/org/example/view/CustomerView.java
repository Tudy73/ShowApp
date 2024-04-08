package org.example.view;

import lombok.Setter;
import org.example.controller.CustomerController;
import org.example.controller.ProductButton;
import org.example.model.product.Pile;
import org.example.model.product.Product;
import org.example.model.security.Role;
import org.example.model.security.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.BoxLayout.Y_AXIS;

public class CustomerView extends JFrame {

    private JTextField tfUsername;
    private JButton btnCheckout;
    private JPanel productListPanel;
    private JPanel cartListPanel;
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    @Setter
    private ActionListener addListener,removeListener;

    public CustomerView() throws HeadlessException {
        setSize(400, 400);
        setLocationRelativeTo(null);
        setTitle("Guest Page");
        setLocation(1070, 0);
        initializeFields();
        setLayout(new BoxLayout(getContentPane(), Y_AXIS));
        add(productListPanel);
        add(cartListPanel);
    }

    private void initializeFields() {
        tfUsername = new JTextField();
        btnCheckout = new JButton("Continue as Guest");
        productListPanel = new JPanel();
        cartListPanel = initCartPanel();
    }

    private JPanel initCartPanel() {
        cartListPanel = new JPanel(new BorderLayout());
        cartTableModel = new DefaultTableModel(new Object[]{"Name", "Price", "Amount"}, 0);
        cartTable = new JTable(cartTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table cells non-editable
            }
        };
        cartListPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Checkout button setup
        btnCheckout = new JButton("Checkout");
        cartListPanel.add(btnCheckout, BorderLayout.SOUTH);
        return cartListPanel;
    }

    public String getUsername() {
        return tfUsername.getText();
    }


    public void setCheckoutButtonListener(ActionListener registerButtonListener) {
        btnCheckout.addActionListener(registerButtonListener);
    }

    public void setVisible() {
        this.setVisible(true);
    }

    public void updateCart(List<Pile> piles) {
        cartTableModel.setRowCount(0);

        for (Pile pile : piles) {
            Product product = pile.getProduct();
            String name = product.getName();
            double price = product.getPrice();
            long amount = pile.getAmount();
            cartTableModel.addRow(new Object[]{name, String.format("$%.2f", price), amount});
        }
        cartTable.revalidate();
        cartTable.repaint();
    }

    public void resetCart() {
        updateCart(new ArrayList<>());
    }

    public void initProductTable(List<Product> productList) {
        productListPanel.setLayout(new GridLayout(0, 1));
        HeadPanel headPanel = new HeadPanel(List.of("Id","Name","Price"),new Dimension(35,20));
        productListPanel.add(headPanel);

        for (Product product : productList) {
            JPanel productPanel = new JPanel();
            productPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel nameLabel = new JLabel(product.getName());
            JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));

            ProductButton addButton = new ProductButton("Add",product.getId());
            ProductButton removeButton = new ProductButton("Remove",product.getId());
            JLabel idLabel = new JLabel(String.valueOf(product.getId()));
            addButton.addActionListener(addListener);
            removeButton.addActionListener(removeListener);
            productPanel.add(idLabel);
            productPanel.add(nameLabel);
            productPanel.add(priceLabel);
            productPanel.add(addButton);
            productPanel.add(removeButton);
            productListPanel.add(productPanel);
        }
        productListPanel.revalidate(); // Refresh the panel layout with the new components
        productListPanel.repaint(); // Ensure the panel is repainted
    }
}
