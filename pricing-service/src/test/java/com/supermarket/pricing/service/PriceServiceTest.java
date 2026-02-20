package com.supermarket.pricing.service;

import com.supermarket.pricing.dto.PriceCalculationRequest;
import com.supermarket.pricing.dto.PriceRequest;
import com.supermarket.pricing.entity.Price;
import com.supermarket.pricing.exception.ResourceNotFoundException;
import com.supermarket.pricing.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PriceService Unit Tests")
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    private PriceRequest validPriceRequest;

    @BeforeEach
    void setUp() {
        validPriceRequest = PriceRequest.builder()
                .sku("SKU001")
                .unitPrice(new BigDecimal("2.99"))
                .currency("USD")
                .build();
    }

    @Nested
    @DisplayName("createPrice")
    class CreatePriceTests {

        @Test
        void shouldCreatePriceSuccessfully() {
            var price = Price.builder()
                    .sku("SKU001")
                    .unitPrice(new BigDecimal("2.99"))
                    .currency("USD")
                    .build();
            when(priceRepository.save(any())).thenReturn(price);

            var response = priceService.createPrice(validPriceRequest);

            assertThat(response.getSku()).isEqualTo("SKU001");
            assertThat(response.getUnitPrice()).isEqualByComparingTo("2.99");
            assertThat(response.getCurrency()).isEqualTo("USD");
        }
    }

    @Nested
    @DisplayName("getPrice")
    class GetPriceTests {

        @Test
        void shouldReturnPriceWhenExists() {
            var price = Price.builder()
                    .sku("SKU001")
                    .unitPrice(new BigDecimal("2.99"))
                    .currency("USD")
                    .build();
            when(priceRepository.findById("SKU001")).thenReturn(Optional.of(price));

            var response = priceService.getPrice("SKU001");

            assertThat(response.getSku()).isEqualTo("SKU001");
            assertThat(response.getUnitPrice()).isEqualByComparingTo("2.99");
        }

        @Test
        void shouldThrowWhenPriceNotFound() {
            when(priceRepository.findById("INVALID")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> priceService.getPrice("INVALID"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("INVALID");
        }
    }

    @Nested
    @DisplayName("calculatePrices")
    class CalculatePricesTests {

        @Test
        void shouldCalculateSubtotalCorrectly() {
            var price = Price.builder()
                    .sku("SKU001")
                    .unitPrice(new BigDecimal("2.99"))
                    .currency("USD")
                    .build();
            when(priceRepository.findById("SKU001")).thenReturn(Optional.of(price));

            var request = PriceCalculationRequest.builder()
                    .items(Map.of("SKU001", 3))
                    .build();

            var response = priceService.calculatePrices(request);

            assertThat(response.getSubtotal()).isEqualByComparingTo("8.97");
            assertThat(response.getItemPrices()).containsEntry("SKU001", new BigDecimal("8.97"));
            assertThat(response.getCurrency()).isEqualTo("USD");
        }
    }

    @Nested
    @DisplayName("deletePrice")
    class DeletePriceTests {

        @Test
        void shouldDeletePrice() {
            var price = Price.builder()
                    .sku("SKU001")
                    .unitPrice(BigDecimal.ONE)
                    .currency("USD")
                    .build();
            when(priceRepository.findById("SKU001")).thenReturn(Optional.of(price));

            priceService.deletePrice("SKU001");

            verify(priceRepository).delete(any());
        }
    }
}
