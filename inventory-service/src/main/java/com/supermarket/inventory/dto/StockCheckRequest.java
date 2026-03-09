package com.supermarket.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCheckRequest {

    @NotNull(message = "Items to check cannot be null")
    @NotEmpty(message = "At least one item is required")
    private Map<String, Integer> items;
}
