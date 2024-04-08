package org.example.repository.product;

public abstract class ProductRepositoryDecorator implements ProductRepository {

  protected ProductRepository decoratedRepository;

  public ProductRepositoryDecorator(ProductRepository productRepository) {
    this.decoratedRepository = productRepository;
  }
}
