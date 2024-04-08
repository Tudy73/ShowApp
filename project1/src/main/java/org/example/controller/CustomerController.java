package org.example.controller;

import lombok.Getter;
import org.example.model.product.Pile;
import org.example.model.product.Product;
import org.example.model.security.User;
import org.example.service.roles.CustomerService;
import org.example.view.CustomerView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CustomerController {
    @Getter
    private final CustomerView view;
    @Getter
    private final CustomerService customerService;

    private final Optional<User> user;

    public CustomerController(CustomerView view, CustomerService customerService, Optional<User> user) {
        this.view = view;
        this.customerService = customerService;
        this.user = user;

        List<Product> productList = customerService.findAllProducts();
        view.setCheckoutButtonListener(new CheckOutButtonListener());
        view.setAddListener(new AddButtonListener());
        view.setRemoveListener(new RemoveButtonListener());
        view.initProductTable(productList);

        if (user.isPresent()) {
            view.setTitle("Customer Page");
            List<Pile> piles = customerService.findProductsForUser(user.get());
            view.updateCart(piles);
        } else {
            view.setTitle("Guest Page");
        }
    }

    public class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            long id = ((ProductButton)e.getSource()).getId();
            customerService.incrementProductForUser(user.get().getId(), id);
            List<Pile> piles = customerService.findProductsForUser(user.get());
            view.updateCart(piles);
        }
    }

    public class RemoveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            long id = ((ProductButton)e.getSource()).getId();
            customerService.decrementProductForUser(user.get().getId(), id);
            List<Pile> piles = customerService.findProductsForUser(user.get());
            view.updateCart(piles);
        }
    }

    public class CheckOutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (user.isPresent()) {
                if (customerService.orderNotFound(user.get().getId())) {
                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                    customerService.addOrder(user.get().getId());
                    Runnable task = () -> {
                        if (customerService.orderNotFound(user.get().getId())) {
                            view.resetCart();
                            scheduler.shutdown();
                            if (!scheduler.isShutdown()) {
                                try {
                                    // Wait a while for existing tasks to terminate
                                    if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                                        scheduler.shutdownNow(); // Cancel currently executing tasks
                                    }
                                } catch (InterruptedException ie) {
                                    // Preserve interrupt status
                                    Thread.currentThread().interrupt();
                                    scheduler.shutdownNow();
                                }
                            }
                        }
                    };

                    // Execute the task every second starting immediately
                    scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
                }
            } else {
                view.resetCart();
            }
        }
    }
}
