package com.supermarket.discounts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    @Id
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    private BigDecimal value;

    private String targetSku;

    private String targetCategory;

    private int minQuantity;

    private BigDecimal minPurchaseAmount;

    private LocalDate validFrom;

    private LocalDate validTo;

    private boolean active;

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT,
        BUY_X_GET_Y
    }
}
