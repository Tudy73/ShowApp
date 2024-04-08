package org.example.repository.product;

import org.example.model.product.Pile;
import org.example.model.product.Product;
import org.example.model.security.User;
import org.example.repository.Cache;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryCacheDecorator extends ProductRepositoryDecorator {

  private final Cache<Product> cache;

  public ProductRepositoryCacheDecorator(ProductRepository productRepository) {
    super(productRepository);
    cache = new Cache<>();
  }

  @Override
  public List<Product> findAll() {
    if (cache.hasResult()) {
      return cache.load();
    }
    List<Product> allProducts = decoratedRepository.findAll();
    cache.save(allProducts);
    return allProducts;
  }

  @Override
  public Optional<Product> findById(Long id) {
    if (cache.hasResult()) {
      return cache.load().stream()
          .filter(product -> product.getId().equals(id))
          .findFirst();
    }
    final Optional<Product> result = decoratedRepository.findById(id);
    if (result.isPresent()) {
      cache.add(result.get());
      return result;
    }
    return Optional.empty();
  }

  @Override
  public Product create(Product product) throws SQLException {
    Product res = decoratedRepository.create(product);
    cache.add(product);
    return res;
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
