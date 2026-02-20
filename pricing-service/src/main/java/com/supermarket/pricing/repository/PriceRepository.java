package com.supermarket.pricing.repository;

import com.supermarket.pricing.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, String> {
}
