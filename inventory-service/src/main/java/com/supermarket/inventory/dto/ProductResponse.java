package com.supermarket.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String sku;
    private String name;
    private String category;
    private int quantity;
    private int reorderThreshold;
}
