package com.supermarket.inventory.repository;

import com.supermarket.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByCategory(String category);

    List<Product> findByQuantityLessThanEqual(int quantity);
}
