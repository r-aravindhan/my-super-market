package com.supermarket.discounts.dto;

import com.supermarket.discounts.entity.Discount;
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
public class DiscountResponse {

    private String code;
    private String description;
    private Discount.DiscountType type;
    private BigDecimal value;
    private String targetSku;
    private String targetCategory;
    private int minQuantity;
    private BigDecimal minPurchaseAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private boolean active;
}
