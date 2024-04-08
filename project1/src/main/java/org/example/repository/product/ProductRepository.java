package org.example.repository.product;

import org.example.model.product.Pile;
import org.example.model.product.Product;
import org.example.model.security.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

  List<Product> findAll();

  Optional<Product> findById(Long id);

  Product create(Product product) throws SQLException;

  List<Pile> findCart(long userId);
  Pile findProductAndUser(User user, Product product);

  void decrementProductFromCart(long user_id, long product_id);

  void incrementProductInCart(long user_id, long product_id);

  void deleteCart(long user_id);
}
