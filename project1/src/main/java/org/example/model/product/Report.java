package org.example.model.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Report {
    private Long soldItems;
    private Double totalMoney;
    private Long noCustomers;
}
