package com.supermarket.pricing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "prices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {

    @Id
    private String sku;

    private BigDecimal unitPrice;

    private String currency;
}
