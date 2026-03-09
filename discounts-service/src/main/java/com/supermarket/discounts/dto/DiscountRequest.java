package com.supermarket.discounts.dto;

import com.supermarket.discounts.entity.Discount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest {

    @NotBlank(message = "Discount code is required")
    private String code;

    private String description;

    @NotNull(message = "Discount type is required")
    private Discount.DiscountType type;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0", message = "Value must be non-negative")
    private BigDecimal value;

    private String targetSku;

    private String targetCategory;

    @Min(value = 0, message = "Min quantity must be non-negative")
    private int minQuantity;

    @DecimalMin(value = "0", message = "Min purchase amount must be non-negative")
    private BigDecimal minPurchaseAmount;

    private LocalDate validFrom;

    private LocalDate validTo;

    private boolean active;
}
