package com.supermarket.inventory.service;

import com.supermarket.inventory.dto.ProductRequest;
import com.supermarket.inventory.dto.StockCheckRequest;
import com.supermarket.inventory.dto.StockCheckResponse;
import com.supermarket.inventory.dto.StockReservationRequest;
import com.supermarket.inventory.exception.InsufficientStockException;
import com.supermarket.inventory.exception.ResourceNotFoundException;
import com.supermarket.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest validProductRequest;

    @BeforeEach
    void setUp() {
        validProductRequest = ProductRequest.builder()
                .sku("SKU001")
                .name("Test Product")
                .category("Test")
                .quantity(100)
                .reorderThreshold(10)
                .build();
    }

    @Nested
    @DisplayName("createProduct")
    class CreateProductTests {

        @Test
        void shouldCreateProductSuccessfully() {
            var product = com.supermarket.inventory.entity.Product.builder()
                    .sku("SKU001")
                    .name("Test Product")
                    .category("Test")
                    .quantity(100)
                    .reorderThreshold(10)
                    .build();
            when(productRepository.existsById("SKU001")).thenReturn(false);
            when(productRepository.save(any())).thenReturn(product);

            var response = productService.createProduct(validProductRequest);

            assertThat(response.getSku()).isEqualTo("SKU001");
            assertThat(response.getName()).isEqualTo("Test Product");
            assertThat(response.getQuantity()).isEqualTo(100);
        }

        @Test
        void shouldThrowWhenProductExists() {
            when(productRepository.existsById("SKU001")).thenReturn(true);

            assertThatThrownBy(() -> productService.createProduct(validProductRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("getProduct")
    class GetProductTests {

        @Test
        void shouldReturnProductWhenExists() {
            var product = com.supermarket.inventory.entity.Product.builder()
                    .sku("SKU001")
                    .name("Test Product")
                    .category("Test")
                    .quantity(100)
                    .reorderThreshold(10)
                    .build();
            when(productRepository.findById("SKU001")).thenReturn(Optional.of(product));

            var response = productService.getProduct("SKU001");

            assertThat(response.getSku()).isEqualTo("SKU001");
            assertThat(response.getName()).isEqualTo("Test Product");
        }

        @Test
        void shouldThrowWhenProductNotFound() {
            when(productRepository.findById("INVALID")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProduct("INVALID"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("INVALID");
        }
    }

    @Nested
    @DisplayName("checkStock")
    class CheckStockTests {

        @Test
        void shouldReturnAvailableWhenStockSufficient() {
            var product = com.supermarket.inventory.entity.Product.builder()
                    .sku("SKU001")
                    .quantity(100)
                    .build();
            when(productRepository.findById("SKU001")).thenReturn(Optional.of(product));

            var request = StockCheckRequest.builder()
                    .items(Map.of("SKU001", 50))
                    .build();

            StockCheckResponse response = productService.checkStock(request);

            assertThat(response.isAvailable()).isTrue();
            assertThat(response.getInsufficientItems()).isEmpty();
        }

        @Test
        void shouldReturnUnavailableWhenStockInsufficient() {
            var product = com.supermarket.inventory.entity.Product.builder()
                    .sku("SKU001")
                    .quantity(10)
                    .build();
            when(productRepository.findById("SKU001")).thenReturn(Optional.of(product));

            var request = StockCheckRequest.builder()
                    .items(Map.of("SKU001", 50))
                    .build();

            StockCheckResponse response = productService.checkStock(request);

            assertThat(response.isAvailable()).isFalse();
            assertThat(response.getInsufficientItems()).containsKey("SKU001");
        }
    }

    @Nested
    @DisplayName("reserveStock")
    class ReserveStockTests {

        @Test
        void shouldThrowWhenInsufficientStock() {
            var product = com.supermarket.inventory.entity.Product.builder()
                    .sku("SKU001")
                    .quantity(5)
                    .build();
            when(productRepository.findById("SKU001")).thenReturn(Optional.of(product));

            var request = StockReservationRequest.builder()
                    .reservationId("RES-001")
                    .items(Map.of("SKU001", 10))
                    .build();

            assertThatThrownBy(() -> productService.reserveStock(request))
                    .isInstanceOf(InsufficientStockException.class);
        }
    }

    @Nested
    @DisplayName("deleteProduct")
    class DeleteProductTests {

        @Test
        void shouldDeleteProduct() {
            var product = com.supermarket.inventory.entity.Product.builder()
                    .sku("SKU001")
                    .name("Test")
                    .category("Test")
                    .quantity(10)
                    .build();
            when(productRepository.findById("SKU001")).thenReturn(Optional.of(product));

            productService.deleteProduct("SKU001");

            verify(productRepository).delete(any());
        }
    }
}
