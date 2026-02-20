package com.supermarket.discounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountApplicationRequest {

    @NotNull(message = "Items cannot be null")
    @NotEmpty(message = "At least one item is required")
    private Map<String, Integer> items;

    @NotNull(message = "Item prices cannot be null")
    private Map<String, BigDecimal> itemPrices;

    @NotNull(message = "Subtotal is required")
    private BigDecimal subtotal;

    private String discountCode;
}
