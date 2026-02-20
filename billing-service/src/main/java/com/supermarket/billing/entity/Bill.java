package com.supermarket.billing.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "bills")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    private String id;

    @ElementCollection
    @CollectionTable(name = "bill_items", joinColumns = @JoinColumn(name = "bill_id"))
    @MapKeyColumn(name = "sku")
    @Column(name = "quantity")
    private Map<String, Integer> items;

    @ElementCollection
    @CollectionTable(name = "bill_item_prices", joinColumns = @JoinColumn(name = "bill_id"))
    @MapKeyColumn(name = "sku")
    @Column(name = "price")
    private Map<String, BigDecimal> itemPrices;

    private BigDecimal subtotal;

    private BigDecimal discountAmount;

    private BigDecimal total;

    private String currency;

    private String appliedDiscountCode;

    private Instant createdAt;
}
