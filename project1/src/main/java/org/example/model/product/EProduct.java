package org.example.model.product;

import lombok.Getter;

@Getter
public enum EProduct {
    PRODUCT1("Salt", 10.99),
    PRODUCT2("Sugar", 9.99),
    PRODUCT3("Milk", 11.99);


    private final String name;
    private final double price;

    EProduct(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public Product getProduct(){
        return new Product(name,price);
    }
}
