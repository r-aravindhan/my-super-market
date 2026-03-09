package com.supermarket.pricing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal unitPrice;

    @NotBlank(message = "Currency is required")
    private String currency;
}
