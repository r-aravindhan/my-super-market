package com.supermarket.inventory.service;

import com.supermarket.inventory.dto.ProductRequest;
import com.supermarket.inventory.dto.ProductResponse;
import com.supermarket.inventory.dto.StockCheckRequest;
import com.supermarket.inventory.dto.StockCheckResponse;
import com.supermarket.inventory.dto.StockReservationRequest;
import com.supermarket.inventory.entity.Product;
import com.supermarket.inventory.exception.InsufficientStockException;
import com.supermarket.inventory.exception.ResourceNotFoundException;
import com.supermarket.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsById(request.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + request.getSku() + " already exists");
        }
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .category(request.getCategory())
                .quantity(request.getQuantity())
                .reorderThreshold(request.getReorderThreshold() > 0 ? request.getReorderThreshold() : 10)
                .build();
        return toResponse(productRepository.save(product));
    }

    public ProductResponse getProduct(String sku) {
        return toResponse(findBySku(sku));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(String sku) {
        Product product = findBySku(sku);
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse updateProduct(String sku, ProductRequest request) {
        Product product = findBySku(sku);
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
        product.setReorderThreshold(request.getReorderThreshold() > 0 ? request.getReorderThreshold() : 10);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void updateStock(String sku, int quantityDelta) {
        Product product = findBySku(sku);
        int newQuantity = product.getQuantity() + quantityDelta;
        if (newQuantity < 0) {
            throw new InsufficientStockException(
                    "Insufficient stock for " + sku,
                    Map.of(sku, "Requested: " + Math.abs(quantityDelta) + ", Available: " + product.getQuantity())
            );
        }
        product.setQuantity(newQuantity);
        productRepository.save(product);
    }

    public StockCheckResponse checkStock(StockCheckRequest request) {
        Map<String, String> insufficientItems = new HashMap<>();

        for (Map.Entry<String, Integer> entry : request.getItems().entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product == null) {
                insufficientItems.put(entry.getKey(), "Product not found");
            } else if (product.getQuantity() < entry.getValue()) {
                insufficientItems.put(entry.getKey(),
                        "Requested: " + entry.getValue() + ", Available: " + product.getQuantity());
            }
        }

        return StockCheckResponse.builder()
                .available(insufficientItems.isEmpty())
                .insufficientItems(insufficientItems)
                .build();
    }

    @Transactional
    public void reserveStock(StockReservationRequest request) {
        StockCheckRequest checkRequest = StockCheckRequest.builder()
                .items(request.getItems())
                .build();
        StockCheckResponse checkResponse = checkStock(checkRequest);

        if (!checkResponse.isAvailable()) {
            throw new InsufficientStockException(
                    "Cannot reserve stock - insufficient quantity",
                    checkResponse.getInsufficientItems()
            );
        }

        request.getItems().forEach((sku, quantity) ->
                updateStock(sku, -quantity)
        );
    }

    @Transactional
    public void releaseReservation(StockReservationRequest request) {
        request.getItems().forEach((sku, quantity) ->
                updateStock(sku, quantity)
        );
    }

    private Product findBySku(String sku) {
        return productRepository.findById(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", sku));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .sku(product.getSku())
                .name(product.getName())
                .category(product.getCategory())
                .quantity(product.getQuantity())
                .reorderThreshold(product.getReorderThreshold())
                .build();
    }
}
