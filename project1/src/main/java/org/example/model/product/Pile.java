package org.example.model.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pile {
    private Product product;
    private Long amount;
}
