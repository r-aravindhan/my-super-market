package com.supermarket.inventory.controller;

import com.supermarket.inventory.dto.ProductRequest;
import com.supermarket.inventory.dto.ProductResponse;
import com.supermarket.inventory.dto.StockCheckRequest;
import com.supermarket.inventory.dto.StockCheckResponse;
import com.supermarket.inventory.dto.StockReservationRequest;
import com.supermarket.inventory.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProduct(sku));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(productService.getProductsByCategory(category));
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{sku}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String sku,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(sku, request));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String sku) {
        productService.getProduct(sku);
        productService.deleteProduct(sku);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/stock/check")
    public ResponseEntity<StockCheckResponse> checkStock(@Valid @RequestBody StockCheckRequest request) {
        return ResponseEntity.ok(productService.checkStock(request));
    }

    @PostMapping("/stock/reserve")
    public ResponseEntity<Void> reserveStock(@Valid @RequestBody StockReservationRequest request) {
        productService.reserveStock(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/stock/release")
    public ResponseEntity<Void> releaseReservation(@Valid @RequestBody StockReservationRequest request) {
        productService.releaseReservation(request);
        return ResponseEntity.ok().build();
    }
}
