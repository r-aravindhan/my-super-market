package com.supermarket.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCheckRequest {

    @NotNull(message = "Items to check cannot be null")
    @NotEmpty(message = "At least one item is required")
    private Map<String, Integer> items;
}
