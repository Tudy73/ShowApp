package org.example.repository.product;

import org.example.model.product.Pile;
import org.example.model.product.Product;
import org.example.model.security.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryMock implements ProductRepository {

  private final List<Product> products;

  public ProductRepositoryMock() {
    products = new ArrayList<>();
  }

  @Override
  public List<Product> findAll() {
    return products;
  }

  @Override
  public Optional<Product> findById(Long id) {
    return products.stream()
        .filter(it -> it.getId().equals(id))
        .findFirst();
  }

  @Override
  public Product create(Product product) {
    products.add(product);
    return product;
  }

  @Override
  public List<Pile> findCart(long userId) {
    return null;
  }

  @Override
  public Pile findProductAndUser(User user, Product product) {
    return null;
  }

  @Override
  public void decrementProductFromCart(long user_id, long product_id) {

  }

  @Override
  public void incrementProductInCart(long user_id, long product_id) {

  }

  @Override
  public void deleteCart(long user_id) {

  }

}
