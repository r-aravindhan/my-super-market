package com.supermarket.pricing.dto;

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
public class PriceCalculationRequest {

    @NotNull(message = "Items cannot be null")
    @NotEmpty(message = "At least one item is required")
    private Map<String, Integer> items;
}
