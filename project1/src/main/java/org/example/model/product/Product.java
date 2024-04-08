package org.example.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
  private Long id;
  private String name;
  private Double price;

  // Custom constructor (without @Builder) for specific fields
  public Product(String name, Double price){
    this.name = name;
    this.price = price;
  }
}
