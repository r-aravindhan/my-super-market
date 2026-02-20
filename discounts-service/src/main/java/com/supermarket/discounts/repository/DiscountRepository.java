package com.supermarket.discounts.repository;

import com.supermarket.discounts.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, String> {

    List<Discount> findByActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqual(
            LocalDate date, LocalDate date2);

    List<Discount> findByTargetSkuAndActiveTrue(String sku);

    List<Discount> findByTargetCategoryAndActiveTrue(String category);
}
